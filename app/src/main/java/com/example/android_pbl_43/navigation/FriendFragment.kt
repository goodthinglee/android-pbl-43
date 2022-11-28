package com.example.android_pbl_43.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android_pbl_43.R
import com.example.android_pbl_43.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlinx.android.synthetic.main.fragment_friend.*
import kotlinx.android.synthetic.main.fragment_friend.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import kotlinx.android.synthetic.main.item_friend.view.*
import kotlin.collections.ArrayList

class FriendFragment : Fragment() {
    var myUid: String? = null
    var firestore: FirebaseFirestore? = null


    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        //uid = FirebaseAuth.getInstance().currentUser?.uid
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_friend, container, false)
        firestore = FirebaseFirestore.getInstance()
        //val recyclerView = view.findViewById<RecyclerView>(R.id.friend_recycler)
        view.friend_recycler.layoutManager = LinearLayoutManager(activity)
        view.friend_recycler.adapter = FriendRecyclerViewAdapter()


//      val recyclerView : RecyclerView = view!!.findViewById<RecyclerView>(R.id.home_recycler)
//      recyclerView.adapter = RecyclerViewAdapter()
//      recyclerView.layoutManager = LinearLayoutManager(inflater.context) //this는 액티비티에서 사용가능 inflater.context는 됨

        return view
    }

    inner class FriendRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var friend: ArrayList<ContentDTO.Friend> = arrayListOf()

        init {
                firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    friend.clear()
                    //contentUidList.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        val item = snapshot.toObject(ContentDTO.Friend::class.java)
                        friend.add(item!!)
                        //contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
            }



        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
            return CustomViewHolder(view)
        }



        override fun getItemCount(): Int {
            return friend.size
        }



        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val viewHolder = (holder as CustomViewHolder).itemView

            viewHolder.friend_item_email.text =
                friend!![position].userId

            Glide.with(holder.itemView.context).load(
                friend!![position].imageUrl
            )
                .apply(RequestOptions().circleCrop())
                .into(viewHolder.friend_item_iv)


            viewHolder.friend_item_iv.setOnClickListener {
                val fragment = UserFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", friend[position].uid)
                bundle.putString("userId", friend[position].userId)

                fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit()
            }
/*
            firestore?.collection("profileImages")?.document(
                friend[position]
                    .uid!!
            )?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result["image"]
                    Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(
                            RequestOptions()
                                .circleCrop()
                        )
                        .into(viewHolder.detailviewitem_profile_image)
                }
            }
*/
            /*
            Glide.with(holder.itemView.context).load(friend[position].profileImageUrl)
                .apply(RequestOptions().circleCrop())
                .into(holder.imageView)
            holder.textViewEmail.text = friend[position].email

            holder.itemView.setOnClickListener{
                val intent = Intent(context, UserFragment::class.java)
                intent.putExtra("destinationUid", friend[position].uid)
                context?.startActivity(intent)
            }
            */
        }


    }
}