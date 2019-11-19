package com.example.wtfwar.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.wtfwar.R
import com.example.wtfwar.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*

class UsernameSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_username_activity)

        val username = findViewById<EditText>(R.id.username)

        val clear = findViewById<ImageView>(R.id.clear)
        clear.setOnClickListener {
            username.text.clear()

//            val input = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            input.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }

        val cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            this.finish()
        }

        val apply = findViewById<TextView>(R.id.apply)
        apply.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(user!!.uid)
            val setName = UserProfileChangeRequest.Builder()
            val name = username.text.toString().trim()

            if (name.length > 5 && !(name.equals(user.displayName))){

                user.updateProfile(setName
                    .setDisplayName(name)
//                    .setPhotoUri(Uri.parse(
//                    "https://firebasestorage.googleapis.com/v0/b/wtfwar-aa218.appspot.com/o/Screenshot_3.png?alt=media&token=98fba220-426c-4f5d-92de-bba23b733f1f"))
                    .build()
                )

                userRef.runTransaction(object: Transaction.Handler{
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val t = mutableData.getValue(UserModel::class.java)
                            ?: return Transaction.success(mutableData)

                        t.name = name

                        mutableData.value = t
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {

                    }
                })

                Toast.makeText(this, "Username changed!", Toast.LENGTH_SHORT).show()
                finish()
            }else{

                if (name.length < 6){
                    Toast.makeText(this, "Username is too short, enter at least 6 characters", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "No changes", Toast.LENGTH_SHORT).show()
                }
            }
        }



    }
}