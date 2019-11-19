package com.example.wtfwar

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.wtfwar.signFragments.SignInFragment
import com.example.wtfwar.signFragments.SignUpFragment
import com.google.firebase.auth.FirebaseAuth

class AuthorizeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val signUpFragment = SignUpFragment()
    private val signInFragment = SignInFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_container)

        mAuth = FirebaseAuth.getInstance()

        val currentUser = mAuth.currentUser
        if (currentUser!=null){
            //start main screen
            startLogin()
        }else {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.login_container, signUpFragment)
            transaction.commit()

        }
    }

    fun regToLogin(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.login_container, signInFragment)
        transaction.commit()
    }

    fun loginToReg(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.login_container, signUpFragment)
        transaction.commit()
    }


    fun startLogin(){
        startActivity(Intent(this, MenuActivity::class.java))
    }
}