package com.example.wtfwar.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.wtfwar.R
import com.example.wtfwar.models.CommentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.comment.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommentsAdapter(context: Context, comments: TreeMap<String, CommentModel>, link: String):RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    var mComm = comments
    var mLink = link
    var commentsList = ArrayList<CommentModel>()
    var mContext = context
    var replies = TreeMap<String, CommentModel>()
    var vief: View? = null
    private lateinit var commentsQuery: DatabaseReference
    val user = FirebaseAuth.getInstance().currentUser!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment, parent, false)
        vief = view
        commentsList.clear()
        for (comment in mComm) {
            commentsList.add(comment.value)
        }

        return CommentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComm.count()
    }


    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {

        val comment = commentsList[position]


//        val authorRef = FirebaseDatabase.getInstance().reference.child("users").child(comment.author!!).child("name")
//        authorRef.addListenerForSingleValueEvent(object: ValueEventListener {
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                holder.author.text = dataSnapshot.value.toString()
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })


        holder.reply.setOnClickListener {

            commentsQuery = FirebaseDatabase.getInstance().reference.child("tweets").child(mLink)
                .child("comments").child(comment.uid).child("replies")
            val msg = CommentModel(comment.uid,user.uid + ": ", "kek")
            commentsQuery.push().setValue(msg.toMap())

        }

        holder.author.text = comment.author
        holder.comment.text = comment.comment

        if (comment.replies.count() > 0){

            replies = TreeMap(comment.replies)

            val repliesAdapter = CommentsAdapter(mContext, replies, mLink)
            val layoutManager = LinearLayoutManager(mContext)
            vief!!.reply_recycler.layoutManager = layoutManager
            vief!!.reply_recycler.adapter = repliesAdapter

        }
    }

    class CommentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var comment: TextView = itemView.findViewById(R.id.text)
        var author: TextView = itemView.findViewById(R.id.author)
        var replies: RecyclerView = itemView.findViewById(R.id.reply_recycler)
        var reply: TextView = itemView.findViewById(R.id.reply)
    }
}