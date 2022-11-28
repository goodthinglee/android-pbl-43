package com.example.android_pbl_43.navigation.model

data class FollowDTO(
        var followerCount : Int = 0,
        var followers : MutableMap<String?, Boolean> = hashMapOf(),
        var followingCount : Int = 0,
        var followings : MutableMap<String, Boolean> = hashMapOf()
)