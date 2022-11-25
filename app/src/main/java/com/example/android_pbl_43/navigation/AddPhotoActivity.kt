package com.example.android_pbl_43.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.android_pbl_43.R
import com.example.android_pbl_43.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    lateinit var storage: FirebaseStorage
    var photoUri: Uri? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 앨범 열기
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // 이미지 업로드 이벤트
        var addphoto_btn_upload = findViewById<Button>(R.id.addphoto_btn_upload)
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                var addphoto_image = findViewById<ImageView>(R.id.addphoto_image)
                addphoto_image.setImageURI(photoUri)
            } else {
                finish()
            }
        }
    }

    fun contentUpload() {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // 이미지 업로드
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            //Insert download of image //﻿다운로드 URL를 컨텐츠 DTO에 넣어주고
            contentDTO.imageUrl = uri.toString()

            //Insert uid of user //﻿유저 아이디를 uid에 넣고
            contentDTO.uid = auth?.currentUser?.uid

            //Insert userId //﻿유저 아이디에 이메일을 넣어준다.
            contentDTO.userId = auth?.currentUser?.email

            //Insert explain of content //사용자가 입력한 설명글을 넣어준다.
            contentDTO.explain = addphoto_edit_explain.text.toString()

            //Insert timestamp //작성한 시간을 넣어준다.
            contentDTO.timestamp = System.currentTimeMillis().toString()

            //컨텐츠 DTO를 images 안에 넣어줄것입니다.
            firestore?.collection("images")?.document()?.set(contentDTO)

            //정상적으로 종료되었다는 프래그값을 넘겨주기 위함.
            setResult(Activity.RESULT_OK)

            //finish //종료하고 창을 닫아줍니다.
            finish()
        }
    }
}