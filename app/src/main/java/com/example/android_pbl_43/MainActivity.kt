package com.example.android_pbl_43

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_pbl_43.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        var bottom_navigation: BottomNavigationView= findViewById(R.id.bottom_navigation)
        bottom_navigation.setOnNavigationItemSelectedListener { it ->
            when(it.itemId) {
                R.id.action_home -> {
                    var detailViewFragment = DetailViewFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, detailViewFragment).commit()
                    true
                }
                R.id.action_search -> {
                    var gridFragment = GridFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, gridFragment).commit()
                    true
                }
                R.id.action_add_photo -> {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(Intent(this, AddPhotoActivity::class.java))
                    }
                    true
                }
                R.id.action_favorite_alarm -> {
                    var alaramFragment = AlarmFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, alaramFragment).commit()
                    true
                }
                R.id.action_account -> {
                    var userFragment = UserFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment).commit()
                    true
                }
                else -> false
            }
        }
    }
}