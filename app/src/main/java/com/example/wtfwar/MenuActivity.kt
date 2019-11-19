package com.example.wtfwar

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.*
import com.example.wtfwar.adapters.TopicsAdapter
import com.example.wtfwar.adapters.TweetsAdapter
import com.example.wtfwar.models.TopicModel
import com.example.wtfwar.models.TweetModel
import com.example.wtfwar.models.UserModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.menu_content.*
import kotlinx.android.synthetic.main.menu_content.posts_recycler
import kotlin.collections.ArrayList

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var back_pressed: Long = 0
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var user: FirebaseUser
    private lateinit var topicsDBRef: DatabaseReference
    private lateinit var topicQuery: Query
    var topicsList = ArrayList<TopicModel>()
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var topicsAdapter: TopicsAdapter

    var tweetsList = ArrayList<TweetModel>()
    lateinit var tweetsAdapter: TweetsAdapter
    lateinit var tweetsDBRef: DatabaseReference
    private lateinit var tweetsQuery: Query
    private lateinit var postsListener: ValueEventListener
    private lateinit var topicsListener: ValueEventListener
    var achieveList = ArrayList<String>()
    private lateinit var shimmer: ShimmerFrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Home"

        shimmer = findViewById(R.id.shimmer)

        user = FirebaseAuth.getInstance().currentUser!!

//        val size = Point()
//        windowManager.defaultDisplay.getSize(size)
//        if ((size.x/size.y) >= 2){
//            posts_recycler.layoutParams.height = posts_recycler.layoutParams.height*2
//        }

        topicsDBRef = FirebaseDatabase.getInstance().reference.child("topics")
        topicQuery = topicsDBRef.orderByChild("date").limitToFirst(2)

        tweetsDBRef = FirebaseDatabase.getInstance().reference.child("tweets")
        tweetsQuery = tweetsDBRef.limitToFirst(2)

        topicsAdapter = TopicsAdapter(this, topicsList)
        layoutManager = LinearLayoutManager(this)
        latest_recycler.layoutManager = layoutManager
        latest_recycler.adapter = topicsAdapter

        tweetsAdapter = TweetsAdapter(this, tweetsList)
        val layoutManager = LinearLayoutManager(this)
        posts_recycler.layoutManager = layoutManager
        posts_recycler.adapter = tweetsAdapter


        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.app_name, R.string.app_name)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val logout = findViewById<TextView>(R.id.logout)
        logout.setOnClickListener{

            mAuth.signOut()
            val intent = Intent(this, AuthorizeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        val share = findViewById<TextView>(R.id.share)
        share.setOnClickListener {
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            val shareSub = "Всем привет, здрасте"
            val shareBody = "Подписывайтесь на канал, ставьте лайки"
            myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(myIntent, "Share using"))
        }

        val donate = findViewById<ImageView>(R.id.donate)
        donate.setOnClickListener {
            startActivity(Intent(this, DonateActivity::class.java))
        }

        loadAchieves()
    }

    override fun onResume() {
        super.onResume()

        shimmer.visibility = View.VISIBLE
        shimmer.startShimmer()

        loadLatestTopics()
        loadNewPosts()
        loadUserStats()

    }

    override fun onPause() {
        super.onPause()

        shimmer.stopShimmer()
        tweetsQuery.removeEventListener(postsListener)
        topicQuery.removeEventListener(topicsListener)
    }

    private fun loadAchieves() {
        val achieveRef = FirebaseDatabase.getInstance().reference.child("achievements").child("list")
        achieveRef.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                achieveList.clear()
                for (child in dataSnapshot.children){
                    achieveList.add(child.value.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MenuActivity, "Failed to load", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loadUserStats(){
        val userQuery = FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val stats = dataSnapshot.getValue(UserModel::class.java)

                val navigationView = findViewById<NavigationView>(R.id.nav_view)
                val header = navigationView.getHeaderView(0)

                val username = findViewById<TextView>(R.id.name)
                val headerUsername = header.findViewById<TextView>(R.id.header_name)
                //username.text = stats!!.name
                username.text = user.displayName
                headerUsername.text = user.displayName

                val currency = findViewById<TextView>(R.id.currency)
                val headerCurrency = header.findViewById<TextView>(R.id.header_currency)
                currency.text = stats!!.currency.toString()
                headerCurrency.text = stats.currency.toString()

                val disputes = findViewById<TextView>(R.id.disputes)
                disputes.text = stats.disputes.toString()

                val winrate = findViewById<TextView>(R.id.winrate)
                if (stats.disputes.equals(0)){
                    winrate.text = String.format("%2d%%", 0)
                }else{
                    winrate.text = String.format("%2d%%", stats.wins*100 / stats.disputes)
                }

                val experience = findViewById<ProgressBar>(R.id.level_progress)
                val headerExp = header.findViewById<ProgressBar>(R.id.header_level_progress)
                experience.progress = stats.experience / (100*61)
                headerExp.progress = stats.experience / (100*61)

                updateAchievements(stats.currency, stats.disputes, stats.experience)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MenuActivity, "Failed to load user stats", Toast.LENGTH_SHORT).show()
            }

        })

        val storageRef = FirebaseStorage.getInstance().reference.child("avatars/"+user.uid)
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val avatar = findViewById<ImageView>(R.id.profile)
            avatar.setImageBitmap(bmp)
            val headerAvatar = findViewById<ImageView>(R.id.header_profile)
            headerAvatar.setImageBitmap(bmp)
//            val ob = BitmapDrawable(resources, bmp)
//            avatar.background = ob

//            Handler().postDelayed({
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
//            }, 1000)

        }.addOnFailureListener {

        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("uid", user.uid)
                startActivity(intent)
            }

            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }

            R.id.nav_disputs -> {
                startActivity(Intent(this, DialogsActivity::class.java))
            }

            R.id.nav_topics -> {
                startActivity(Intent(this, TopicsActivity::class.java))
            }

            R.id.nav_posts -> {
                startActivity(Intent(this, TweetsActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun loadLatestTopics() {
        topicsListener = topicQuery.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                topicsList.clear()

                for (tweetsSnapshot in dataSnapshot.children){
                    val topic = tweetsSnapshot.getValue(TopicModel::class.java)
                    topicsList.add(topic!!)
                }

                topicsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MenuActivity, "Failed to load topics", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loadNewPosts() {
        postsListener = tweetsQuery.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tweetsList.clear()
                for (tweetsSnapshot in dataSnapshot.children){
                    val tweet = tweetsSnapshot.getValue(TweetModel::class.java)
                    tweetsList.add(tweet!!)
                }
                tweetsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MenuActivity, "Failed to load",Toast.LENGTH_SHORT).show()
            }

        })
    }



    private fun updateAchievements(currency: Int, disputes: Int, experience: Int){
        //TODO: уведомление

        val key = "currency"
        val userRef = FirebaseDatabase.getInstance().reference.child("achievements").child(user.uid)
        val userAchieveList = ArrayList<String>()
        userAchieveList.add("first")
        userAchieveList.add("second")
        val point = 50

        val wo = object : Runnable {
            override fun run() {
                achieveList.forEach {
                    when (key){
                        "currency" -> {
                            if ((currency >= point) && !(userAchieveList.contains(it))){
                                userAchieveList.add(it)
                                userRef.push().setValue(it)
                            }
                        }

                        "disputes" -> {

                        }

                        "experience" -> {

                        }
                    }
                }
            }
        }.run()
    }

    private fun checkDaily(currency: Int, disputes: Int){

        val userRef = FirebaseDatabase.getInstance().reference.child("daily").child(user.uid)

        //load user

        var state: String ?= null
        state = "complete"

        if (state == null){
            setDaily(currency, disputes)
        }


        val key = "disputes"
        val goal = 50

        if (!state.equals("complete")){
            val wo = object : Runnable {
                override fun run() {

                    when (key){
                        "currency" -> {
                            if (currency>=goal){
                                /**
                                 *
                                 * currency +transact
                                 * state = "complete"
                                 *
                                 *
                                 */
                            }

                        }

                        "disputes" -> {

                        }

                        "experience" -> {

                        }
                    }
                }
            }.run()
        }

    }

    private fun setDaily(currency: Int, disputes: Int){
        val dailyRef = FirebaseDatabase.getInstance().reference.child("daily").child("goal")
        //load daily

        val key = "disputes"
        val point = 5

        val userRef = FirebaseDatabase.getInstance().reference.child("daily").child(user.uid)

        //put daily

        /**
         *
         * key = "disputes"
         * goal = disputes + point
         * state = "progress"
         *
         */

    }



    fun onTopicClick(view: View) {
        startActivity(Intent(this, TopicsActivity::class.java))
    }

    fun onTweetClick(view: View) {
        startActivity(Intent(this, TweetsActivity::class.java))
    }

    override fun onBackPressed(){

        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{

            if (back_pressed + 2000 > System.currentTimeMillis()){

                //super.onBackPressed()
                moveTaskToBack(true)
            }else{
                Toast.makeText(this@MenuActivity, "Press again to exit", Toast.LENGTH_SHORT).show()
            }
            back_pressed = System.currentTimeMillis()
        }
    }
}



//осталось
//сделать дейлик как ачивки (но все это хранить в фб как-то фу)
//че с донатом сделать
//мб поиск тела сделать в сервис, хз
//что делать когда чел закроет приложение находясь в q или если оба закроют в диалоге