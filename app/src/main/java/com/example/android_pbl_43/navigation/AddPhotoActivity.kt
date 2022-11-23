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
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    lateinit var storage: FirebaseStorage
    var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage = FirebaseStorage.getInstance()

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
        if(requestCode == PICK_IMAGE_FROM_ALBUM) {
            if(resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                var addphoto_image = findViewById<ImageView>(R.id.addphoto_image)
                addphoto_image.setImageURI(photoUri)
            }else {
                finish()
            }
        }
    }

    fun contentUpload() {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // 이미지 업로드
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
        }
    }
}