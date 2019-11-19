package com.example.wtfwar

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.wtfwar.settings.AvatarSettings
import com.example.wtfwar.settings.UsernameSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SettingsActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.word_settings)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        user = FirebaseAuth.getInstance().currentUser!!

        val username = findViewById<RelativeLayout>(R.id.username)
        username.setOnClickListener{
            startActivity(Intent(this, UsernameSettings::class.java))
        }

//        val email = findViewById<RelativeLayout>(R.id.email)
//        email.setOnClickListener{
//            startActivity(Intent(this, EmailSettings::class.java))
//        }
//
//        val password = findViewById<RelativeLayout>(R.id.password)
//        password.setOnClickListener{
//            startActivity(Intent(this, PasswordSettings::class.java))
//        }
//
        val avatar = findViewById<ImageView>(R.id.profile)
        avatar.setOnClickListener{
            startActivity(Intent(this, AvatarSettings::class.java))
        }
//
//        val username = findViewById<RelativeLayout>(R.id.username)
//        username.setOnClickListener{
//            startActivity(Intent(this, UsernameSettings::class.java))
//        }
//
//        val username = findViewById<RelativeLayout>(R.id.username)
//        username.setOnClickListener{
//            startActivity(Intent(this, UsernameSettings::class.java))
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId==android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}