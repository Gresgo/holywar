package com.example.wtfwar.adapters

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.wtfwar.QueueActivity
import com.example.wtfwar.R
import com.example.wtfwar.models.TopicModel
import com.example.wtfwar.services.FastBlur
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TopicsAdapter(context: Activity, topics: ArrayList<TopicModel>) :
    RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {

    private var mTopics = topics
    private val mContext = context
    private lateinit var user: FirebaseUser
    private lateinit var qRef: DatabaseReference
    private lateinit var eRef: DatabaseReference
    private lateinit var emRef: DatabaseReference
    private lateinit var mRef: DatabaseReference
    private lateinit var qry: Query
    private lateinit var mQry: Query
    private val fastBlur = FastBlur(mContext)
    private lateinit var alert: AlertDialog
    private lateinit var builder: AlertDialog.Builder
    private val timer = Timer()
    private lateinit var reason: String
    private lateinit var newRef: DatabaseReference
    private lateinit var awaitRef: DatabaseReference
    private lateinit var await: ChildEventListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.topic, parent, false)
        user = FirebaseAuth.getInstance().currentUser!!

        return TopicsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mTopics.size
    }

    override fun onBindViewHolder(holder: TopicsViewHolder, position: Int) {

        val topic = mTopics[position]

//        holder.topicLeft.text = topic.left
//        holder.topicRight.text = topic.right
        holder.title.text = topic.name
        holder.nameLeft.text = topic.left
        holder.nameRight.text = topic.right
        if (topic.leftUsers.contains(user.uid)){
            holder.activeLeft.visibility = View.VISIBLE
            holder.activeRight.visibility = View.GONE
        }
        if (topic.rightUsers.contains(user.uid)){
            holder.activeRight.visibility = View.VISIBLE
            holder.activeLeft.visibility = View.GONE
        }

        if ((topic.voteLeft+topic.voteRight) == 0){
            holder.procLeft.text = String.format("%2d%%", 50)
            holder.procRight.text = String.format("%2d%%", 50)
        }else{
            holder.procLeft.text = String.format("%2d%%", topic.voteLeft*100 / (topic.voteLeft+topic.voteRight))
            holder.procRight.text = String.format("%2d%%", topic.voteRight*100 / (topic.voteLeft+topic.voteRight))
        }

        val topicRef = FirebaseDatabase.getInstance().reference.child("topics").child(topic.name)
        val userVotes = FirebaseDatabase.getInstance().reference.child("user-votes").child(user.uid)

        holder.voteLeft.setOnClickListener {
            if (!topic.leftUsers.contains(user.uid) && !topic.rightUsers.contains(user.uid)) {

                topicRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val t = mutableData.getValue(TopicModel::class.java)
                            ?: return Transaction.success(mutableData)

                        t.voteLeft = t.voteLeft + 1
                        t.leftUsers[user.uid] = true

                        mutableData.value = t
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                    }
                })

                userVotes.child(topic.name).setValue("left")
                holder.activeLeft.visibility = View.VISIBLE

            }else{
                if (!topic.leftUsers.contains(user.uid) && topic.rightUsers.contains(user.uid)) return@setOnClickListener
            }

                val builder = AlertDialog.Builder(mContext).setTitle("Почему")
                val dialogView: View = mContext.layoutInflater.inflate(R.layout.whythis, null)
                val edit = dialogView.findViewById<EditText>(R.id.editText)
                builder.setView(dialogView)
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("GO") { dialog, which ->
                        dialog.dismiss()
                        reason = edit.text.toString()
                        eRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("left")
                            .child("q")
                        qRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("right")
                            .child("q")
                        emRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("left")
                            .child("msg")
                        mRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("right")
                            .child("msg")


                        newRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("left")
                        startQueue(topic.name)
                    }.show()
//           startToUid("oooaaammm32281488")

        }

        holder.voteRight.setOnClickListener {
            if (!topic.leftUsers.contains(user.uid) && !topic.rightUsers.contains(user.uid)) {

                topicRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val t = mutableData.getValue(TopicModel::class.java)
                            ?: return Transaction.success(mutableData)

                        t.voteRight = t.voteRight + 1
                        t.rightUsers[user.uid] = true

                        mutableData.value = t
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                    }
                })

                userVotes.child(topic.name).setValue("right")
                holder.activeRight.visibility = View.VISIBLE

            }else{
                if (topic.leftUsers.contains(user.uid) && !topic.rightUsers.contains(user.uid)) return@setOnClickListener
            }

                val builder = AlertDialog.Builder(mContext).setTitle("Почему")
                val dialogView: View = mContext.layoutInflater.inflate(R.layout.whythis, null)
                val edit = dialogView.findViewById<EditText>(R.id.editText)
                builder.setView(dialogView)
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("GO") { dialog, which ->
                        dialog.dismiss()
                        reason = edit.text.toString()
                        eRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("right")
                            .child("q")
                        qRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("left")
                            .child("q")
                        emRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("right")
                            .child("msg")
                        mRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("left")
                            .child("msg")


                        newRef = FirebaseDatabase.getInstance().reference.child("queue").child(topic.name).child("right")
                        startQueue(topic.name)
                    }.show()
    //            startToUid("oooaaammm32281488")
        }

        holder.voteLeft.setOnLongClickListener{

            if (topic.leftUsers.contains(user.uid)){

                topicRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val t = mutableData.getValue(TopicModel::class.java)
                            ?: return Transaction.success(mutableData)

                        t.voteLeft = t.voteLeft - 1
                        t.leftUsers.remove(user.uid)

                        mutableData.value = t
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                    }
                })

                holder.activeLeft.visibility = View.GONE
                userVotes.child(topic.name).removeValue()

            }


            true
        }

        holder.voteRight.setOnLongClickListener{

            if (topic.rightUsers.contains(user.uid)){

                topicRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val t = mutableData.getValue(TopicModel::class.java)
                            ?: return Transaction.success(mutableData)

                        t.voteRight = t.voteRight - 1
                        t.rightUsers.remove(user.uid)

                        mutableData.value = t
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                    }
                })

                holder.activeRight.visibility = View.GONE
                userVotes.child(topic.name).removeValue()

            }


            true
        }

    }


    private fun startQueue(topic: String){

        newRef.child(user.uid).setValue("inq")
        showQueue(topic)

        awaitRef = FirebaseDatabase.getInstance().reference.child("messages")
        await = awaitRef.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(dataSnapshot: DataSnapshot, parent: String?) {

                if (dataSnapshot.key!!.contains(user.uid)){
                    awaitRef.removeEventListener(this)
                    alert.cancel()
                    timer.purge()
                    startToUid(dataSnapshot.key.toString())
                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })

    }

//
//    //START TOPIC CLICK
//
//    //checking if anyone already in queue
//    private fun startQueue(topic: String) {
//
//        qry = eRef.limitToFirst(1)
//        qry.addListenerForSingleValueEvent(object : ValueEventListener {
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val choose = dataSnapshot.value
//
//                if (choose != null) {
//                    dataSnapshot.ref.removeValue()
//                    choose as HashMap<String, String>
//                    val list = choose.toList()
//                    if (list[0].first != user.uid) {
//                        //start chat with user in q
//                        callQueue(list[0].first)
//                    } else {
//                        //if user is himself check again LUL
//                        startQueue(topic)
//                    }
//                } else {
//                    //put user in q
//                    qRef.child(user.uid).setValue("inq")
//                    startChat(topic)
//                }
//
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }
//
//    //in q listener - gets next user call to start
//    private fun startChat(topic: String) {
//        showQueue(topic)
//        Toast.makeText(mContext, "started", Toast.LENGTH_SHORT).show() //don't really needed for now
//
//        mQry = mRef.orderByKey().equalTo(user.uid).limitToFirst(1)
//        mQry.addChildEventListener(object : ChildEventListener {
//
//            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
//
//                if (dataSnapshot.key == user.uid) {
//                    //dialog ID
//                    val friend: String = dataSnapshot.value.toString() + dataSnapshot.key
//                    dataSnapshot.ref.removeValue()
//                    mQry.removeEventListener(this)
//                    //start messaging
//                    alert.cancel()
//                    timer.purge()
//                    startToUid(friend)
//                }
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                //Toast.makeText(this@MessagesActivity, "Failed",Toast.LENGTH_SHORT).show()
//                //idk what to type here
//            }
//
//        })
//
//    }
//
////Здесь нужна вторая проверка
//    //а лучше вообще все вынести в отдельный сервис нахуй
//
//    //call user in q
//    private fun callQueue(uid: String) {
//        emRef.child(uid).setValue(user.uid)
//        val friend: String = user.uid + uid
//        //start messaging
//        startToUid(friend)
//    }





    private fun startToUid(friend: String) {
        val intent = Intent(mContext, QueueActivity::class.java)
        intent.putExtra("friend", friend)
//        intent.putExtra("reason", reason)
        mContext.startActivity(intent)
    }
    //END TOPIC CLICK

    private fun showQueue(topic: String){
        builder = AlertDialog.Builder(mContext, R.style.CustomDialogTheme)
            .setCancelable(false)
        alert = builder.create()
        val dialogView: View = mContext.layoutInflater.inflate(R.layout.alert_queue, null)

        val timerCount = dialogView.findViewById<TextView>(R.id.timer)
        val topicName = dialogView.findViewById<TextView>(R.id.queue_topic_name)
        topicName.text = topic

        val button: Button = dialogView.findViewById(R.id.dialog_cancel)
        button.setOnClickListener{
            alert.cancel()
            timer.purge()
//            qRef.child(user.uid).removeValue()
            awaitRef.removeEventListener(await)
            newRef.child(user.uid).removeValue()

        }

        alert.setView(dialogView)
        val map = fastBlur.getBitmapFromView(mContext.window.decorView)
        val draw = BitmapDrawable(mContext.resources, map)
        alert.window!!.setBackgroundDrawable(draw)
        alert.show()

        var value = 0
        timer.scheduleAtFixedRate(object: TimerTask(){

            override fun run() {
                value += 1

                mContext.runOnUiThread {
                    timerCount.text = String.format("%02d:%02d", value % 3600/60, value % 60)
                }

            }
        },1000,1000)
    }


    class TopicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        val topic: TextView = itemView.findViewById(R.id.topic_name_left)
//        val topicLeft: TextView = itemView.findViewById(R.id.topic_name_left)
//        val topicRight: TextView = itemView.findViewById(R.id.topic_name_right)
        val title: TextView = itemView.findViewById(R.id.topic_title)
        val nameLeft: TextView = itemView.findViewById(R.id.topic_left)
        val nameRight: TextView = itemView.findViewById(R.id.topic_right)
        val voteLeft: Button = itemView.findViewById(R.id.vote_left)
        val voteRight: Button = itemView.findViewById(R.id.vote_right)
        val procLeft: TextView = itemView.findViewById(R.id.proc_left)
        val procRight: TextView = itemView.findViewById(R.id.proc_right)
        val activeLeft: ImageView = itemView.findViewById(R.id.left_active)
        val activeRight: ImageView = itemView.findViewById(R.id.right_active)

    }
}