package com.example.wtfwar

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerTabStrip
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.wtfwar.models.UserModel
import com.example.wtfwar.profileFragments.AchievementsFragment
import com.example.wtfwar.profileFragments.PostsFragment
import com.example.wtfwar.profileFragments.VotesFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.profile)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        uid = intent.getStringExtra("uid")
//        user = FirebaseAuth.getInstance().currentUser!!

        val adapter = Adapter(supportFragmentManager)

        val viewPager: ViewPager = findViewById(R.id.pager)
        viewPager.adapter = adapter
        viewPager.currentItem = 1
        val strip: PagerTabStrip = findViewById(R.id.pagerTabStrip)
        strip.drawFullUnderline = false
    }

    override fun onResume() {
        super.onResume()

        loadUserStats()
    }

    fun getUid(): String{
        return uid
    }

    private fun loadUserStats(){
        val userQuery = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val stats = dataSnapshot.getValue(UserModel::class.java)

                val username = findViewById<TextView>(R.id.name)
                username.text = stats!!.name
//                username.text = user.displayName

                val disputes = findViewById<TextView>(R.id.disputes)
                disputes.text = stats.disputes.toString()

                val winrate = findViewById<TextView>(R.id.winrate)
                if (stats.disputes.equals(0)){
                    winrate.text = String.format("%2d%%", 0)
                }else{
                    winrate.text = String.format("%2d%%", stats.wins*100 / stats.disputes)
                }

                val experience = findViewById<ProgressBar>(R.id.level_progress)
                experience.progress = stats.experience / (100*61)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load user stats", Toast.LENGTH_SHORT).show()
            }

        })

        val storageRef = FirebaseStorage.getInstance().reference.child("avatars/"+uid)
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val avatar = findViewById<ImageView>(R.id.profile)
            avatar.setImageBitmap(bmp)
//            val ob = BitmapDrawable(resources, bmp)
//            avatar.background = ob
        }.addOnFailureListener {

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId==android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    class Adapter internal constructor(fm: FragmentManager): FragmentPagerAdapter(fm){

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment?=null
            when (position){
                0 -> fragment = AchievementsFragment()
                1 -> fragment = PostsFragment()
                2 -> fragment = VotesFragment()
            }

            return fragment
        }

        override fun getCount(): Int {

            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position){
                0 -> return "Achievements"
                1 -> return "Posts"
                2 -> return "Votes"
            }
            return "Tab"
        }
    }
}