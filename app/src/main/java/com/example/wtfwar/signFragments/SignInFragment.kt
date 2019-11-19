package com.example.wtfwar.signFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.wtfwar.R
import com.google.firebase.auth.FirebaseAuth
import com.example.wtfwar.AuthorizeActivity


class SignInFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private val TAG = "LoginActivity"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.sign_in, container, false)

        mAuth = FirebaseAuth.getInstance()

        val emailView = view.findViewById<EditText>(R.id.email)
        val passwordView = view.findViewById<EditText>(R.id.password)

        val changeFragment = view.findViewById<TextView>(R.id.changeFragment)
        changeFragment.setOnClickListener{
            loginToReg()
        }

        val onpass = view.findViewById<CheckBox>(R.id.onpass)
        onpass.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                passwordView.transformationMethod = null

            } else {
                passwordView.transformationMethod = PasswordTransformationMethod()
            }
        }

        val signIn = view.findViewById<Button>(R.id.signIn)
        signIn.setOnClickListener{
            signIn(emailView.text.toString().trim(),passwordView.text.toString().trim())
        }

        return view
    }

    private fun loginToReg(){
        (activity as AuthorizeActivity).loginToReg()
    }

    private fun signIn(email: String, password: String) {
        //Log.e(TAG, "signIn:" + email)
        if (!validateForm(email, password)) {
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.e(TAG, "signIn: Success!")

                    startLogin()

                } else {
                    Log.e(TAG, "signIn: Fail!", task.exception)
                    Toast.makeText(activity, "Authentication failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateForm(email: String, password: String): Boolean {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(activity, "Enter email address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(activity, "Enter password!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun startLogin(){
        (activity as AuthorizeActivity).startLogin()
    }
}