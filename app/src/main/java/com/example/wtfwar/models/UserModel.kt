package com.example.wtfwar.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserModel(
    var name: String? = "",
    var email: String? = "",
    var pass: String? = "",
    var currency: Int = 0,
    var disputes: Int = 0,
    var wins: Int = 0,
    var achievements: ArrayList<String> = ArrayList(),
    var experience: Int = 0
)