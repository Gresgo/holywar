package com.example.wtfwar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.wtfwar.adapters.TweetsAdapter
import com.example.wtfwar.models.TweetModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.tweets_content.*

class TweetsActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    var tweetsList = ArrayList<TweetModel>()
    lateinit var tweetsAdapter: TweetsAdapter
    lateinit var tweetsDBRef: DatabaseReference
    lateinit var usertweetsDBRef: DatabaseReference
    private lateinit var tweetsQuery: Query


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tweets_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.posts)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        user = FirebaseAuth.getInstance().currentUser!!

        tweetsAdapter = TweetsAdapter(this, tweetsList)
        val layoutManager = LinearLayoutManager(this)
        tweets_recycler.layoutManager = layoutManager
        tweets_recycler.adapter = tweetsAdapter


        tweetsDBRef = FirebaseDatabase.getInstance().reference.child("tweets")
        usertweetsDBRef = FirebaseDatabase.getInstance().reference.child("user-posts")
        tweetsQuery = tweetsDBRef.limitToFirst(20)

        //load list of topics
            //TODO: same as topics
        tweetsQuery.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tweetsList.clear()
                for (tweetsSnapshot in dataSnapshot.children){
                    val tweet = tweetsSnapshot.getValue(TweetModel::class.java)
                    tweetsList.add(tweet!!)
                }
                tweetsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TweetsActivity, "Failed to load",Toast.LENGTH_SHORT).show()
            }

        })
    }



    fun addTweet(view: View){
//        val ref = tweetsDBRef.push().key
//        val tweet = TweetModel(user.uid, ref, "text")
//        tweetsDBRef.child(ref!!).setValue(tweet.toMap())
//        usertweetsDBRef.child(user.uid).push().setValue(ref)

        Toast.makeText(this@TweetsActivity, "Только дря крутых поцанов",Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId==android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}