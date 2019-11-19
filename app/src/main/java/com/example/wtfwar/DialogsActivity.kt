package com.example.wtfwar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.example.wtfwar.adapters.DialogsAdapter
import com.example.wtfwar.models.DialogModel
import kotlinx.android.synthetic.main.dialogs_content.*


class DialogsActivity : AppCompatActivity() {

    lateinit var dialogsAdapter: DialogsAdapter
    var dialogsList = ArrayList<DialogModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogs_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.dialogs)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val list = fileList().toCollection(ArrayList())

        list.remove("instant-run")

        dialogsAdapter = DialogsAdapter(this, list)
        val layoutManager = LinearLayoutManager(this)
        dialogs_recycler.layoutManager = layoutManager
        dialogs_recycler.adapter = dialogsAdapter

//        val history: TextView = findViewById(R.id.history)
//
//        history.setOnClickListener {
//            var fin: FileInputStream? = null
//            try {
//                fin = openFileInput("friend.dat")
//                val bytes = ByteArray(fin.available())
//                fin.read(bytes)
//                val text = String(bytes)
//                val gson = GsonBuilder().create()
//
////                val list = gson.fromJson(text, MessageModel::class.java)
//
//                val list = gson.fromJson<ArrayList<MessageModel>>(text, object : TypeToken<ArrayList<MessageModel>>(){}.type)
//
//                history.text = list[0].msg
//            }catch (ex: IOException){
//
//                Toast.makeText(this@DialogsActivity, "Failed to load dialog", Toast.LENGTH_SHORT).show()
//            }finally {
//
//                try {
//                    if (fin!=null) fin.close()
//                }catch (ex: IOException){
//
//                    Toast.makeText(this@DialogsActivity, "Failed to save dialog", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

    }




    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            android.R.id.home -> super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}