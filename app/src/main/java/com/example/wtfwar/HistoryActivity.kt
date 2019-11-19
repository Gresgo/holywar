package com.example.wtfwar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.example.wtfwar.adapters.MessageAdapter
import com.example.wtfwar.models.MessageModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.messages_unused.*

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messages_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationIcon(R.drawable.arrow_left)

        val butthurt = findViewById<Button>(R.id.butthurt)
        butthurt.visibility = View.GONE

        val text = intent.getStringExtra("dialog")

        val gson = GsonBuilder().create()
        val dialog = gson.fromJson<ArrayList<MessageModel>>(text, object : TypeToken<ArrayList<MessageModel>>(){}.type)

        val messageAdapter = MessageAdapter(applicationContext, dialog)
        val layoutManager = LinearLayoutManager(applicationContext)
        msg_recycler.layoutManager = layoutManager
        msg_recycler.adapter = messageAdapter
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            android.R.id.home -> super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}