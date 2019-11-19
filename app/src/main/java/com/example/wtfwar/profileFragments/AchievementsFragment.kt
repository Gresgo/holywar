package com.example.wtfwar.profileFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wtfwar.ProfileActivity
import com.example.wtfwar.R
import com.example.wtfwar.adapters.AchievementsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_achieve.view.*

class AchievementsFragment : Fragment() {

    var achieveList = ArrayList<String>()
    lateinit var achieveAdapter: AchievementsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_achieve, container, false)

        val uid = (activity as ProfileActivity).getUid()
        val achieveRef = FirebaseDatabase.getInstance().reference.child("achievements").child(uid)


        achieveAdapter = AchievementsAdapter(activity!!.applicationContext, achieveList)
        val layoutManager = LinearLayoutManager(activity!!.applicationContext)
        view.achieve_recycler.layoutManager = layoutManager
        view.achieve_recycler.adapter = achieveAdapter

        achieveRef.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                achieveList.clear()
                for (child in dataSnapshot.children){
                    achieveList.add(child.value.toString())
                }
                achieveAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show()
            }

        })

        return view
    }
}