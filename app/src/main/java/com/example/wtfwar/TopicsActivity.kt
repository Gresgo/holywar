package com.example.wtfwar

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.wtfwar.adapters.TopicsAdapter
import com.example.wtfwar.models.TopicModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.topics_content.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TopicsActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    //var topicsList = ArrayList<String>()
    var topicsList = ArrayList<TopicModel>()
    lateinit var topicsAdapter: TopicsAdapter
    private lateinit var topicsDBRef: DatabaseReference
    private lateinit var topicQuery: Query
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topics_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
//        supportActionBar!!.title = "List of topics"
//        supportActionBar!!.setHomeButtonEnabled(true)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

//        toolbar.setNavigationIcon(R.drawable.arrow_left)
//        toolbar.setNavigationOnClickListener {
//            super.onBackPressed()
//        }

        supportActionBar!!.setDisplayShowTitleEnabled(false)

        user = FirebaseAuth.getInstance().currentUser!!
        topicsDBRef = FirebaseDatabase.getInstance().reference.child("topics")
        topicQuery = topicsDBRef.limitToFirst(20)

        topicsAdapter = TopicsAdapter(this, topicsList)
        layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        topics_recycler.layoutManager = layoutManager
        topics_recycler.adapter = topicsAdapter

        //load first ??? topics
            //TODO: need to load another ??? when user reaches the end of the list
        topicQuery.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (topicSnapshot in dataSnapshot.children){
//                    topicsList.add(topicSnapshot.key!!)
//                }

                for (tweetsSnapshot in dataSnapshot.children){
                    val topic = tweetsSnapshot.getValue(TopicModel::class.java)
                    topicsList.add(topic!!)
                }

                topicsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TopicsActivity, "Failed to load",Toast.LENGTH_SHORT).show()
            }

        })

        val sortHot = findViewById<Button>(R.id.sort_hot)
        val sortDate = findViewById<Button>(R.id.sort_date)
        val sortRate = findViewById<Button>(R.id.sort_rate)
        val create = findViewById<Button>(R.id.create)
        var prevButton = sortHot
        val textColor = sortHot.textColors

        //TODO: sort list
        sortHot.setOnClickListener{

            prevButton.setBackgroundResource(R.color.absolute)
            prevButton.setHintTextColor(Color.WHITE)
            prevButton = sortHot
            sortHot.setBackgroundResource(R.drawable.button_back)
            sortHot.setHintTextColor(textColor)
//            topicsAdapter.notifyDataSetChanged()
        }

        sortDate.setOnClickListener{

            prevButton.setBackgroundResource(R.color.absolute)
            prevButton.setHintTextColor(Color.WHITE)
            prevButton = sortDate
            sortDate.setBackgroundResource(R.drawable.button_back)
            sortDate.setHintTextColor(textColor)
            topicsList.sortBy { it.date }
            topicsAdapter.notifyDataSetChanged()
        }

        sortRate.setOnClickListener{

            prevButton.setBackgroundResource(R.color.absolute)
            prevButton.setHintTextColor(Color.WHITE)
            prevButton = sortRate
            sortRate.setBackgroundResource(R.drawable.button_back)
            sortRate.setHintTextColor(textColor)
        }

        create.setOnClickListener{v ->

            Toast.makeText(this@TopicsActivity, "Disabled",Toast.LENGTH_SHORT).show()
            //addTopic()
        }

        val back = findViewById<ImageView>(R.id.btn_back)
        back.setOnClickListener {

            super.onBackPressed()
        }

        val search = findViewById<ImageView>(R.id.btn_search)
        search.setOnClickListener {

            val intent = Intent(this, TopicsSearch::class.java)
           // intent.putExtra("topics", topicsList) TODO: чет надо сделать этим
            startActivity(intent)
//            startActivity(Intent(this, TopicsSearch::class.java))
        }
    }


    /*fun addTopic(view: View){
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = false
        topics_recycler.layoutManager = layoutManager
        topics_recycler.adapter = topicsAdapter

        topicQuery = topicsDBRef.orderByKey().limitToFirst(20)

        topicQuery.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                topicsList.clear()
                for (topicSnapshot in dataSnapshot.children){
                    topicsList.add(topicSnapshot.key!!)
                }
                topicsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TopicsActivity, "Failed to load",Toast.LENGTH_SHORT).show()
            }

        })
    }*/

    fun addTopic(){
        val time = SimpleDateFormat("yyMMddHHmmss", Locale.US)
        time.timeZone = TimeZone.getTimeZone("UTC")
        topicsDBRef.child("newtopic").child("date").setValue(time.format(Calendar.getInstance().time))
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.options_menu, menu)
//
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item!!.itemId){
//            android.R.id.home -> super.onBackPressed()
//        }
//        return super.onOptionsItemSelected(item)
//    }
}