package com.example.wtfwar.signFragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.wtfwar.AuthorizeActivity
import com.example.wtfwar.R
import com.example.wtfwar.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment : Fragment() {

    private var setName = UserProfileChangeRequest.Builder()
    private var mHandler = Handler()

    private lateinit var mAuth: FirebaseAuth
    private val TAG = "LoginActivity"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.sign_up, container, false)

        mAuth = FirebaseAuth.getInstance()

        val usernameView = view!!.findViewById<EditText>(R.id.username)
        val emailView = view.findViewById<EditText>(R.id.email)
        val passwordView = view.findViewById<EditText>(R.id.password)
        val passwordConf = view.findViewById<EditText>(R.id.password_conf)


        val changeFragment = view.findViewById<TextView>(R.id.changeFragment)
        changeFragment.setOnClickListener{
            regToLogin()
        }

        val onpass = view.findViewById<CheckBox>(R.id.onpass)
        onpass.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                passwordView.transformationMethod = null

            } else {
                passwordView.transformationMethod = PasswordTransformationMethod()
            }
        }

        val onpassconf = view.findViewById<CheckBox>(R.id.onpass_conf)
        onpassconf.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                passwordConf.transformationMethod = null

            } else {
                passwordConf.transformationMethod = PasswordTransformationMethod()
            }
        }

        val signUp = view.findViewById<Button>(R.id.signUp)
        signUp.setOnClickListener{
            createAcc(emailView.text.toString().trim(), passwordView.text.toString().trim(),
                passwordConf.text.toString().trim(), usernameView.text.toString().trim())
        }

        return view
    }

    fun regToLogin(){
        (activity as AuthorizeActivity).regToLogin()
    }

    private fun createAcc(email: String, password: String, verify: String, username: String) {

        if (!validateForm(email, password, verify, username)) {
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(TAG, "createAcc: Success!")
                    Toast.makeText(activity, "Account Created!", Toast.LENGTH_SHORT).show()

                    val user = mAuth.currentUser

                    user!!.updateProfile(setName
                        .setDisplayName(username)
                        .build())


                    val wo = object : Runnable {
                        override fun run() {
                            if (username!=user.displayName){
                                mHandler.postDelayed(this,100)
                            }else{
                                mHandler.removeCallbacks(this)
                                writeNewUser(user.uid,email, password, username)
                            }
                        }
                    }

                    mHandler.postDelayed(wo,100)

                } else {
                    Log.e(TAG, "createAcc: Fail!", task.exception)
                    Toast.makeText(activity, "Failed :C", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateForm(email: String, password: String, verify: String, username: String): Boolean {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(activity, "Enter email address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(activity, "Enter password!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(activity, "Enter username!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (username.length < 6) {
            Toast.makeText(activity, "Username is too short, enter at least 6 characters!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(activity, "Password is too short, enter at least 6 characters!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!password.equals(verify)){
            Toast.makeText(activity, "Password не совпадает", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    //writing user data to database
    private fun writeNewUser(userId: String, email: String, pass: String, username: String) {

        val user = UserModel(username, email, pass)

        FirebaseDatabase.getInstance().reference.child("users").child(userId).setValue(user)

        startLogin()
    }

    private fun startLogin(){
        (activity as AuthorizeActivity).startLogin()
    }

}