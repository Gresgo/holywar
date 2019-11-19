package com.example.wtfwar.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CommentModel(
    var uid: String = "",
    var author: String? = "",
    var comment: String? = "",
    var replies: MutableMap<String, CommentModel> = HashMap()
){

    @Exclude
    fun toMap(): Map<String, Any>{
        return mapOf(
            "uid" to uid,
            "author" to author!!,
            "comment" to comment!!,
            "replies" to replies
        )
    }

}