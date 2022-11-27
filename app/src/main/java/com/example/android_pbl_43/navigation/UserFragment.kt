package com.example.android_pbl_43.navigation

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android_pbl_43.LoginActivity
import com.example.android_pbl_43.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.android_pbl_43.R
import com.example.android_pbl_43.navigation.model.ContentDTO
import com.example.android_pbl_43.navigation.model.FollowDTO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.view.*
class UserFragment : Fragment() {
    var fragmentView : View? = null
    lateinit var firestore: FirebaseFirestore
    var uid : String? = null
    var auth : FirebaseAuth? = null
    var userId : String? = null
    var currentUid : String? = null
    lateinit var storeage : FirebaseStorage
    var followDTO = FollowDTO()
    companion object {
       var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storeage = FirebaseStorage.getInstance()
        currentUid = auth?.currentUser?.uid
        userId = arguments?.getString("userId")
        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerviewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(requireActivity(),3)
        var mainactivity = (activity as MainActivity)
        if(uid == currentUid) {
            mainactivity.toolbar_title_image?.visibility = View.VISIBLE
            mainactivity.toolbar_username?.visibility = View.INVISIBLE
            mainactivity.toolbar_btn_back?.visibility = View.INVISIBLE
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener{
                auth?.signOut()
                activity?.finish()
                startActivity(Intent(activity,LoginActivity::class.java))
            }
            fragmentView?.account_iv_profile?.setOnClickListener {
                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
            }
        } else {
            mainactivity.toolbar_title_image?.visibility = View.INVISIBLE
            mainactivity.toolbar_username?.visibility = View.VISIBLE
            mainactivity.toolbar_btn_back?.visibility = View.VISIBLE
            mainactivity.toolbar_username?.text = userId
            mainactivity.toolbar_btn_back?.setOnClickListener {
                mainactivity.bottom_navigation.selectedItemId = R.id.action_home
            }
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                requestFollow()
            }
        }
        getProfileImage()
        getFollow()
        return fragmentView
    }
    fun getFollow(){
        firestore.collection("users").document(uid!!).addSnapshotListener { value, error ->
            if(value == null) return@addSnapshotListener
            followDTO = value.toObject(FollowDTO::class.java)!!
            if(followDTO?.followerCount != null) {
                fragmentView?.account_follower_textview?.text = followDTO?.followerCount.toString()
                if(currentUid == uid){
                    return@addSnapshotListener
                }
                checkIfFragmentAttached {
                    if (followDTO.followers.containsKey(currentUid)) {
                        fragmentView?.account_btn_follow_signout?.text =
                            getString(R.string.follow_cancel)
                        fragmentView?.account_btn_follow_signout?.background?.setColorFilter(
                            ContextCompat.getColor(requireActivity(), R.color.colorLightGray),
                            PorterDuff.Mode.MULTIPLY
                        )
                    } else {
                        fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
                        fragmentView?.account_btn_follow_signout?.background?.colorFilter = null
                    }
                }
            }
            if(followDTO?.followingCount != null){
                fragmentView?.account_following_textview?.text = followDTO.followingCount.toString()
            }
        }
    }
    fun requestFollow(){
        var tsDocFollowing = firestore.collection("users").document(currentUid!!)
        firestore.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true
                transaction.set(tsDocFollowing,followDTO)
                return@runTransaction
            } else if(followDTO.followings.containsKey(uid)){
                followDTO.followingCount = followDTO.followingCount - 1
                followDTO.followings.remove(uid)
            } else {
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing,followDTO)
            return@runTransaction
        }
        var tsDocFollower = firestore.collection("users").document(uid!!)
        firestore.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUid!!] = true
                transaction.set(tsDocFollower,followDTO!!)
                return@runTransaction
            } else if(followDTO!!.followers.containsKey(currentUid)){
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUid)
            } else {
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUid!!] = true
            }
            transaction.set(tsDocFollower,followDTO!!)
            return@runTransaction
        }
    }
    fun getProfileImage(){
        firestore.collection("profileImage").document(uid!!).addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            if (value.data != null) {
                var url = value.data!!["image"]
                Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop())
                    .into(fragmentView?.account_iv_profile!!)
            }
        }
    }
    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }
    inner class UserFragmentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        init {
            firestore.collection("images").whereEqualTo("uid",uid).addSnapshotListener { value, error ->
                if(value == null) return@addSnapshotListener

                for(snapshot in value.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                fragmentView?.account_post_textview?.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }
        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview){}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions()).into(imageview)
        }


        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }
}
