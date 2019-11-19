package com.example.wtfwar.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.wtfwar.HistoryActivity
import com.example.wtfwar.R
import com.google.firebase.auth.FirebaseAuth
import java.io.FileInputStream
import java.io.IOException

class DialogsAdapter(context: Context, dialogs: ArrayList<String>):RecyclerView.Adapter<DialogsAdapter.DialogsViewHolder>(){

    private var mDialogs = dialogs
    private var user = FirebaseAuth.getInstance().currentUser!!.displayName
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_user, parent, false)

        return DialogsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDialogs.size
    }

    override fun onBindViewHolder(holder: DialogsViewHolder, position: Int) {

        val dialog = mDialogs[position]

        holder.name.text = dialog

        holder.name.setOnClickListener {
            var fin: FileInputStream? = null
            try {
                fin = mContext.openFileInput(dialog)
                val bytes = ByteArray(fin.available())
                fin.read(bytes)
                val text = String(bytes)

                val intent = Intent(mContext, HistoryActivity::class.java)
                intent.putExtra("dialog", text)
                mContext.startActivity(intent)

            }catch (ex: IOException){

                Toast.makeText(mContext, "Failed to load dialog", Toast.LENGTH_SHORT).show()
            }finally {

                try {
                    if (fin!=null) fin.close()
                }catch (ex: IOException){

                    Toast.makeText(mContext, "Failed to save dialog", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    class DialogsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        //var avatar: ImageView = itemView.findViewById(R.id.avatar)
        var name: TextView = itemView.findViewById(R.id.username)

    }
}