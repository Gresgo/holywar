package com.example.wtfwar.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MessageModel(
    var from: String? = "",
    var msg: String? = ""
){

    @Exclude
    fun toMap(): Map<String, Any>{
        return mapOf(
            "from" to from!!,
            "msg" to msg!!
        )
    }

}