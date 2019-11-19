package com.example.wtfwar.models

data class DialogModel(
    var name: String? = "",
    var date: String? = "",
    //var avatar: String? = "",
    var dialog: ArrayList<String> = ArrayList()
)