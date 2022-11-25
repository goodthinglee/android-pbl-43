package com.example.android_pbl_43

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android_pbl_43.navigation.AlarmFragment
import com.example.android_pbl_43.navigation.DetailViewFragment
import com.example.android_pbl_43.navigation.GridFragment
import com.example.android_pbl_43.navigation.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

                    true
                }
                R.id.action_favorite_alarm -> {
                    var alaramFragment = AlarmFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, alaramFragment).commit()
                    true
                }
                R.id.action_account -> {
                    var userFragment = UserFragment()
                    var bundle = Bundle()
                    var uid = FirebaseAuth.getInstance().currentUser?.uid
                    bundle.putString("destinationUid",uid)
                    userFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment).commit()
                    true
                }
                else -> false
            }
        }
    }
}