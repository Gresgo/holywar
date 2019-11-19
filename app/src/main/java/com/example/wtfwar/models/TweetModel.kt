package com.example.wtfwar.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TweetModel(
    var author: String? = "",
    var uid: String? = "",
    var text: String? = "",
    var likes: Int = 0,
    var likeUsers: MutableMap<String, Boolean> = HashMap(),
    var dislikes: Int = 0,
    var dislikeUsers: MutableMap<String, Boolean> = HashMap(),
    var comments: MutableMap<String, CommentModel> = HashMap()
){
    @Exclude
    fun toMap(): Map<String, Any>{
        return mapOf(
            "author" to author!!,
            "uid" to uid!!,
            "text" to text!!,
            "likes" to likes,
            "likeUsers" to likeUsers,
            "dislikes" to dislikes,
            "comments" to comments
        )
    }
}