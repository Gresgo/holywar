package com.example.wtfwar.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wtfwar.R
import com.example.wtfwar.models.MessageModel
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(context: Context, messages: ArrayList<MessageModel>):RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){

    private var mMessages = messages
    private var user = FirebaseAuth.getInstance().currentUser!!.displayName
    private val NOT_ME = 0
    private val IS_ME = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        var view: View?=null

        when (viewType) {
            NOT_ME -> view = LayoutInflater.from(parent.context).inflate(R.layout.message, parent,false)
            IS_ME -> view = LayoutInflater.from(parent.context).inflate(R.layout.message_my, parent,false)
        }

        return MessageViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        val msg = mMessages[position]
        val type = getItemViewType(position)

        when (type){
            NOT_ME -> {
                //holder.author!!.text = msg.from
                holder.message!!.text = msg.msg
            }
            IS_ME -> holder.myMessage!!.text = msg.msg
        }
    }

    override fun getItemViewType(position: Int): Int {
        val msg = mMessages[position]
        if (msg.from.equals(user)) return IS_ME
        else return NOT_ME
    }



    class MessageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var message: TextView? = itemView.findViewById(R.id.msg_show)
        var myMessage: TextView? = itemView.findViewById(R.id.msg_me_show)
        //var author: TextView? = itemView.findViewById(R.id.msg_author)
    }
}