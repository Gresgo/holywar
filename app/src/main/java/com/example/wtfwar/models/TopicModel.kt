package com.example.wtfwar.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TopicModel(
    var date: Long = 0,
    var name: String = "",
    var left: String = "",
    var right: String = "",
    var voteLeft: Int = 0,
    var leftUsers: MutableMap<String, Boolean> = HashMap(),
    var voteRight: Int = 0,
    var rightUsers: MutableMap<String, Boolean> = HashMap(),
    var tag1: String = "",
    var tag2: String = "",
    var tag3: String = ""
){
    @Exclude
    fun toMap(): Map<String, Any>{
        return mapOf(
            "date" to date
        )
    }
}