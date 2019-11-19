package com.example.wtfwar

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.wtfwar.adapters.MessageAdapter
import com.example.wtfwar.models.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.messages_unused.*
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.wtfwar.models.UserModel
import com.example.wtfwar.services.FastBlur
import com.google.gson.GsonBuilder
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class QueueActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    private lateinit var dbRef: DatabaseReference
    private lateinit var mEditText: EditText
    var messageList = ArrayList<MessageModel>()
    lateinit var messageAdapter: MessageAdapter
    lateinit var mMessageRecycler: RecyclerView
    lateinit var friend: String
    private lateinit var builder: AlertDialog.Builder
    private var back_pressed: Long = 0
    private lateinit var fastBlur: FastBlur
    private lateinit var userRef: DatabaseReference
    private lateinit var time: TextView
    val timer = Timer()
    var value = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messages_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        time = findViewById(R.id.timer)

        toolbar.setNavigationIcon(R.drawable.arrow_left)

        user = FirebaseAuth.getInstance().currentUser!!
        dbRef = FirebaseDatabase.getInstance().getReference("messages")
        fastBlur = FastBlur(this)

        mEditText = findViewById(R.id.msg_inp)
        mEditText.requestFocus()
        mMessageRecycler = findViewById(R.id.msg_recycler)

        val butthurt = findViewById<Button>(R.id.butthurt)
        butthurt.setOnClickListener {
            dbRef.child(friend).child("loose").setValue(user.uid)
        }

        val send = findViewById<ImageButton>(R.id.send)
        send.setOnClickListener {
            sendMsg()
        }

        friend = intent.getStringExtra("friend")

        userRef = FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
        //addDispute()

        messageAdapter = MessageAdapter(applicationContext, messageList)
        val layoutManager = LinearLayoutManager(applicationContext)
        msg_recycler.layoutManager = layoutManager
        msg_recycler.adapter = messageAdapter

        //messages update listener
//        dbRef.child(friend).child("loose").setValue("anyone")
        updateMessages()
        startTimer()
    }

    private fun addWin(){

        userRef.runTransaction(object: Transaction.Handler{
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val t = mutableData.getValue(UserModel::class.java)
                    ?: return Transaction.success(mutableData)

                t.wins += 1
                t.currency += 1

                mutableData.value = t
                return Transaction.success(mutableData)
            }

            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {

            }
        })
    }

    private fun addDispute(){

        userRef.runTransaction(object: Transaction.Handler{
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val t = mutableData.getValue(UserModel::class.java)
                    ?: return Transaction.success(mutableData)

                t.disputes += 1

                mutableData.value = t
                return Transaction.success(mutableData)
            }

            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {

            }
        })
    }

    private fun updateMessages(){

        dbRef.child(friend).addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

//                restartTimer()
                value = 30

                if (!dataSnapshot.key.equals("loose")){

                    val msg = dataSnapshot.getValue(MessageModel::class.java)

                    messageList.add(msg!!)
                    messageAdapter.notifyDataSetChanged()
                    mMessageRecycler.smoothScrollToPosition(messageList.size)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

                if (dataSnapshot.key.equals("loose")){
                    if (!dataSnapshot.value!!.equals("anyone")){
                        if (dataSnapshot.value!!.equals(user.uid)){

                            dbRef.removeEventListener(this)
                            closeChat()
                        }else{

                            dbRef.removeEventListener(this)
                            onChatEnd()
                        }
                    }
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@QueueActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }


    private fun startTimer(){

        value = 30

        timer.scheduleAtFixedRate(object: TimerTask(){

            override fun run() {

                this@QueueActivity.runOnUiThread {

                    time.text = String.format("%02d:%02d", value % 3600/60, value % 60)
                    if (value == 0){
                        val last = messageList.last()
                        if (last.from.equals(user.uid)){

                            val los = friend.replaceBefore(user.uid,"").replaceAfter(user.uid,"")
                            dbRef.child(friend).child("loose").setValue(los)

                        }else{
                            dbRef.child(friend).child("loose").setValue(user.uid)
                        }
                    }
                    value -= 1
                }

            }
        },1000,1000)



    }

    //send message
    private fun sendMsg(){

        val msg = MessageModel(user.displayName!!, mEditText.text.toString())

        if (msg.msg.equals("")) {
            return
        }

        dbRef.child(friend).push().setValue(msg.toMap())
        mEditText.setText("")
    }

    private fun closeChat() {

        timer.cancel()
        saveDialog()
        //dbRef.child(friend).removeValue()

        if (mEditText.hasFocus()){
            val input = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
//                super.onBackPressed()
        }

        builder = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setCancelable(false)
        val alert = builder.create()
        val dialogView: View = layoutInflater.inflate(R.layout.alert_chat, null)
        val text: TextView = dialogView.findViewById(R.id.state)
        text.setText(R.string.alert_lose)
        val ass: TextView = dialogView.findViewById(R.id.assCount)
        ass.visibility = View.GONE
        val button: Button = dialogView.findViewById(R.id.dialog_continue)
        button.setHint(R.string.okay)
        button.setOnClickListener{
            finish()
            alert.cancel()
        }
        alert.setView(dialogView)
        val map = fastBlur.getBitmapFromView(this.window.decorView)
        val draw = BitmapDrawable(resources, map)
        alert.window!!.setBackgroundDrawable(draw)
        alert.show()

    }

    //if dialog has ended
    private fun onChatEnd(){

        if (!isFinishing) {

            timer.cancel()
            //addWin()
            saveDialog()
            //dbRef.child(friend).removeValue()

            if (mEditText.hasFocus()){
                val input = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
//                super.onBackPressed()
            }

            builder = AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setCancelable(false)
            val alert = builder.create()
            val dialogView: View = layoutInflater.inflate(R.layout.alert_chat, null)

            val button: Button = dialogView.findViewById(R.id.dialog_continue)
            button.setOnClickListener{
                finish()
                alert.cancel()
            }
            alert.setView(dialogView)
            val map = fastBlur.getBitmapFromView(this.window.decorView)
            val draw = BitmapDrawable(resources, map)
            alert.window!!.setBackgroundDrawable(draw)
            alert.show()

        }
    }


    private fun saveDialog(){

        var dialog: String? = ""
        val FILE_NAME = friend

        val wo = object : Runnable {
            override fun run() {

                val gson = GsonBuilder().create()
                dialog = gson.toJson(messageList)

                var fos: FileOutputStream? = null
                try {

                    fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
                    fos.write(dialog!!.toByteArray())
                }catch (ex: IOException){

                    Toast.makeText(this@QueueActivity, "Failed to save dialog", Toast.LENGTH_SHORT).show()
                }finally {
                    try {

                        if (fos!=null) fos.close()
                    }catch (ex: IOException){

                        Toast.makeText(this@QueueActivity, "Failed to save dialog", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.run()
    }


    override fun onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            closeChat()
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
        }
        back_pressed = System.currentTimeMillis()
    }

}