package com.example.android_pbl_43

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn = findViewById<Button>(R.id.email_login_button)
        btn.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.email_edittext).text.toString()
            val password = findViewById<EditText>(R.id.email_edittext).text.toString()
            doLoginandSingUp(userEmail, password)
        }
    }
    override fun onStart() {
        super.onStart()
        moveMainPage(Firebase.auth?.currentUser)
    }
    private fun doLoginandSingUp(userEmail: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password) // 인증 시도
            .addOnCompleteListener(this) {
                if (it.isSuccessful) { // 회원가입 성공 시
                    moveMainPage(it.result.user) // 페이지 이동
                } else if (it.exception?.message.isNullOrEmpty()) {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                } else { // 회원가입 실패 시
                    doLogin(userEmail, password) // 로그인
                }
            }
    }

    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password) // 인증 시도
            .addOnCompleteListener(this) {
                if (it.isSuccessful) { // 로그인 성공 시
                    moveMainPage(it.result.user) // 페이지 이동
                } else { // 로그인 실패 시
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish()
        }
    }



}