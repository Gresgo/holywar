package com.example.wtfwar.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wtfwar.R
import com.google.firebase.auth.FirebaseAuth

class AchievementsAdapter(context: Context, achievements: ArrayList<String>):RecyclerView.Adapter<AchievementsAdapter.AchievementsViewHolder>(){

    private var mAchiev = achievements
    private var user = FirebaseAuth.getInstance().currentUser!!.displayName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.achieve, parent, false)

        return AchievementsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mAchiev.size
    }

    override fun onBindViewHolder(holder: AchievementsViewHolder, position: Int) {

        val achieve = mAchiev[position]

        holder.title.text = achieve
    }

    class AchievementsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var title: TextView = itemView.findViewById(R.id.achieve_title)

    }
}