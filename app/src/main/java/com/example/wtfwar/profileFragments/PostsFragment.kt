package com.example.wtfwar.profileFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wtfwar.ProfileActivity
import com.example.wtfwar.R
import com.example.wtfwar.adapters.TweetsAdapter
import com.example.wtfwar.models.TweetModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_posts.view.*

class PostsFragment :Fragment(){

    var tweetsList = ArrayList<TweetModel>()
    var userPosts = ArrayList<String>()
    lateinit var tweetsAdapter: TweetsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        loadPosts()

        tweetsAdapter = TweetsAdapter(activity!!, tweetsList)
        val layoutManager = LinearLayoutManager(activity!!)
        view.posts_recycler.layoutManager = layoutManager
        view.posts_recycler.adapter = tweetsAdapter

        return view
    }

    private fun loadPosts(){
        val uid = (activity as ProfileActivity).getUid()
        val userPostsQuery = FirebaseDatabase.getInstance().reference.child("user-posts").child(uid)
        val tweetsQuery = FirebaseDatabase.getInstance().reference.child("tweets")


        userPostsQuery.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                tweetsList.clear()
                userPosts.clear()

                for (tweetsSnapshot in dataSnapshot.children){
//                    val tweet = tweetsSnapshot.getValue(TweetModel::class.java)
                    userPosts.add(tweetsSnapshot.value.toString())
                }

                for (userPost in userPosts) {
                    tweetsQuery.child(userPost).addListenerForSingleValueEvent(object: ValueEventListener{

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val tweet = dataSnapshot.getValue(TweetModel::class.java)
                            tweetsList.add(tweet!!)
                            tweetsAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })
                }
                tweetsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }

        })
    }
}