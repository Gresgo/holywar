package com.example.wtfwar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.wtfwar.adapters.CommentsAdapter
import com.example.wtfwar.models.CommentModel
import com.example.wtfwar.models.TweetModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.post_content.*
import java.util.*
import kotlin.collections.ArrayList

class PostActivity : AppCompatActivity() {

    var commentsList = TreeMap<String, CommentModel>()
    lateinit var commentsAdapter: CommentsAdapter
    private lateinit var tweet: String
    private lateinit var tweetAuthor: TextView
    private lateinit var tweetText: TextView
    private lateinit var tweetLikes: TextView
    private lateinit var tweetDislikes: TextView
    private lateinit var tweetComments: TextView
    private lateinit var commentEdit: EditText
    private lateinit var commentSend: Button
    private lateinit var commentsQuery: DatabaseReference
    private lateinit var user: FirebaseUser
    private var postListener: ValueEventListener? = null
    private var postQuery: Query? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.details)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        tweet = intent.getStringExtra("tweet")

        user = FirebaseAuth.getInstance().currentUser!!

        tweetAuthor = findViewById(R.id.tweet_author)
        tweetText = findViewById(R.id.tweet_text)
        tweetLikes = findViewById(R.id.tweet_likes)
        tweetDislikes = findViewById(R.id.tweet_dislikes)
        tweetComments = findViewById(R.id.tweet_comments)
        commentEdit = findViewById(R.id.comment_msg)
        commentSend = findViewById(R.id.comment_send)

        commentSend.setOnClickListener {
            commendSendMessage()
        }

        commentsAdapter = CommentsAdapter(this, commentsList, tweet)
        val layoutManager = LinearLayoutManager(this)
        comments_recycler.layoutManager = layoutManager
        comments_recycler.adapter = commentsAdapter

    }

    override fun onPause() {
        super.onPause()

        postQuery!!.removeEventListener(postListener!!)
    }

    override fun onResume() {
        super.onResume()

        loadTopic()
    }

    private fun loadTopic(){
        commentsQuery = FirebaseDatabase.getInstance().reference.child("tweets").child(tweet).child("comments")

//        val myComm = CommentModel("notme", "cool guy").toMap()
//        commentsQuery.push().setValue(myComm)

//        val listener = commentsQuery.addChildEventListener(object : ChildEventListener {
//
//            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
//
//                val comment = dataSnapshot.getValue(CommentModel::class.java)
//                commentsList.add(comment!!)
//                tweetComments.text = commentsList.count().toString()
//                commentsAdapter.notifyDataSetChanged()
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Toast.makeText(this@PostActivity, "Failed", Toast.LENGTH_SHORT).show()
//            }
//
//        })

        postQuery = FirebaseDatabase.getInstance().reference.child("tweets").child(tweet)

        postListener = postQuery!!.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tweet = dataSnapshot.getValue(TweetModel::class.java)

                    val authorRef = FirebaseDatabase.getInstance().reference.child("users").child(tweet!!.author!!).child("name")
                    authorRef.addListenerForSingleValueEvent(object: ValueEventListener{

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            tweetAuthor.text = dataSnapshot.value.toString()
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })


                tweetText.text = tweet.text
                tweetLikes.text = tweet.likes.toString()
                tweetDislikes.text = tweet.dislikes.toString()
                tweetComments.text = tweet.comments.count().toString()

                commentsList.clear()
                commentsList.putAll(tweet.comments)

                commentsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@PostActivity, "Failed to load",Toast.LENGTH_SHORT).show()
            }

        })





//        commentsQuery.removeEventListener(listener)
    }

    private fun commendSendMessage(){

        if (!commentEdit.text.toString().equals("")){
            val path = commentsQuery.push().key
            val msg = CommentModel(path!!,user.displayName + ": ", commentEdit.text.toString())
            commentsQuery.child(path).setValue(msg.toMap())
            commentEdit.setText("")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId==android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}