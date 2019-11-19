package com.example.wtfwar.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class DailyModel(
    var key: String? = "",
    var point: String? = "",
    var desc: String? = ""
){

    @Exclude
    fun toMap(): Map<String, Any>{
        return mapOf(
            "key" to key!!,
            "point" to point!!,
            "desc" to  desc!!
        )
    }

}