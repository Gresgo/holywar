package com.example.wtfwar


import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.wtfwar.adapters.TopicsAdapter
import com.example.wtfwar.models.TopicModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.topics_search.*

class TopicsSearch : AppCompatActivity(), SearchView.OnQueryTextListener{

    var topicsList = ArrayList<TopicModel>()
    var queryList = ArrayList<TopicModel>()
//    var topicsList = ArrayList<String>()
//    var queryList = ArrayList<String>()
    private lateinit var searchAdapter: TopicsAdapter
    private lateinit var topicsDBRef: DatabaseReference
    private lateinit var topicQuery: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topics_search_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.listOfTopics)
//        supportActionBar!!.setHomeButtonEnabled(true)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


//        topicsList = intent.getStringArrayListExtra("topics")

        toolbar.setNavigationIcon(R.drawable.arrow_left)
        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }

        loadTopics()

        if (Intent.ACTION_SEARCH == intent.action){
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->

            }
        }

        searchAdapter = TopicsAdapter(this, queryList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        search_recycler.layoutManager = layoutManager
        search_recycler.adapter = searchAdapter


    }


    private fun loadTopics(){

        topicsDBRef = FirebaseDatabase.getInstance().reference.child("topics")
        topicQuery = topicsDBRef

        topicQuery.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (topicSnapshot in dataSnapshot.children){
//                    topicsList.add(topicSnapshot.key!!)
//                }

                for (tweetsSnapshot in dataSnapshot.children){
                    val topic = tweetsSnapshot.getValue(TopicModel::class.java)
                    topicsList.add(topic!!)
                }

                //searchAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TopicsSearch, "Failed to load", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.options_menu, menu)
        val item: MenuItem = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(search: String?): Boolean {

        return true
    }

    override fun onQueryTextChange(search: String?): Boolean {


        queryList.clear()
        if (!search.equals("")) {
            topicsList.forEach {

                if (it.tag1.contains(search as CharSequence) || it.tag2.contains(search as CharSequence) ||
                    it.tag3.contains(search as CharSequence) || it.name.contains(search as CharSequence) ||
                    it.left.contains(search as CharSequence) || it.right.contains(search as CharSequence)) {
                    queryList.add(it)

                }
            }
        }
        searchAdapter.notifyDataSetChanged()

        return true
    }
}