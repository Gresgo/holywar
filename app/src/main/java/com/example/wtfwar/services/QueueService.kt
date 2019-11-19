package com.example.wtfwar.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.google.firebase.database.*

class QueueService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val qRef = FirebaseDatabase.getInstance().getReference("topics")

        Toast.makeText(applicationContext, "Hi", Toast.LENGTH_SHORT).show()

        qRef.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val msg = dataSnapshot.getValue(String::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }
        })


        return super.onStartCommand(intent, flags, startId)
    }
}