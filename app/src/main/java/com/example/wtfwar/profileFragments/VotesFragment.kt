package com.example.wtfwar.profileFragments

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wtfwar.ProfileActivity
import com.example.wtfwar.R
import com.example.wtfwar.adapters.TopicsAdapter
import com.example.wtfwar.models.TopicModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_votes.view.*
import java.util.ArrayList

class VotesFragment :Fragment(){

    var topicsList = ArrayList<TopicModel>()
    var userVotes = ArrayList<String>()
    lateinit var topicsAdapter: TopicsAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_votes, container, false)

        loadVotes()

        topicsAdapter = TopicsAdapter(activity as Activity, topicsList)
        val layoutManager = LinearLayoutManager(activity)
//        layoutManager.reverseLayout = true
//        layoutManager.stackFromEnd = true
        view.votes_recycler.layoutManager = layoutManager
        view.votes_recycler.adapter = topicsAdapter

        return view
    }

    private fun loadVotes(){

        val uid = (activity as ProfileActivity).getUid()
        val userVotesQuery = FirebaseDatabase.getInstance().reference.child("user-votes").child(uid)
        val topicsQuery = FirebaseDatabase.getInstance().reference.child("topics")


        userVotesQuery.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                topicsList.clear()
                userVotes.clear()

                for (topicsSnapshot in dataSnapshot.children){
//                    val tweet = tweetsSnapshot.getValue(TweetModel::class.java)
                    userVotes.add(topicsSnapshot.key.toString())
                }

                for (userPost in userVotes) {
                    topicsQuery.child(userPost).addListenerForSingleValueEvent(object: ValueEventListener{

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val tweet = dataSnapshot.getValue(TopicModel::class.java)
                            topicsList.add(tweet!!)
                            topicsAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })
                }
                topicsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }

        })

    }
}