package com.example.android_pbl_43.navigation

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.android_pbl_43.R
import com.example.android_pbl_43.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.net.URL
import java.security.Permissions
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_add_photo.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

   var firestore: FirebaseFirestore? = null
   var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 앨범 열기
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        //addphoto_image.setOnClickListener {
        //    val photoPickerIntent = Intent(Intent.ACTION_PICK)
        //    photoPickerIntent.type = "image/*"
        //   startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        //}

        // 이미지 업로드 이벤트
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) { //사진 선택 시
                photoUri = data?.data //이미지 경로
                addphoto_image.setImageURI(photoUri) //이미지뷰에 표시
            } else {
                finish()
            }
        }
    }

    fun contentUpload() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) //파일 이름
        val imageFileName = "IMAGE_" + timeStamp + "_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // 이미지 업로드
        storageRef?.putFile(photoUri!!)
            ?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl}
        ?.addOnSuccessListener { uri ->
                var contentDTO = ContentDTO()
                contentDTO.imageUrl = uri!!.toString()
                contentDTO.uid = auth?.currentUser?.uid
                contentDTO.explain = addphoto_edit_explain.text.toString()
                contentDTO.userId = auth?.currentUser?.email
                contentDTO.timestamp = System.currentTimeMillis()
                firestore?.collection("images")?.document()?.set(contentDTO)

                setResult(Activity.RESULT_OK)

                finish()
            }
        }




       // }?.addOnFailureListener {
            //Toast.makeText(this, getString(R.string.upload_fail)
            //    , Toast.LENGTH_SHORT).show()
       // }


}