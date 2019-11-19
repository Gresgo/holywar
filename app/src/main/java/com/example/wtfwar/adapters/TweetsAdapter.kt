package com.example.wtfwar.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.wtfwar.PostActivity
import com.example.wtfwar.ProfileActivity
import com.example.wtfwar.R
import com.example.wtfwar.models.TweetModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class TweetsAdapter(context: Context, tweets: ArrayList<TweetModel>): RecyclerView.Adapter<TweetsAdapter.TweetsViewHolder>() {

    private var mTweets = tweets
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.tweet, parent, false)

        return TweetsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mTweets.size
    }

    override fun onBindViewHolder(holder: TweetsViewHolder, position: Int) {

        val tweet = mTweets[position]

        holder.tweetText.text = tweet.text
//        holder.tweetAuthor.text = tweet.name
        holder.tweetLikes.text = tweet.likes.toString()
        holder.tweetDislikes.text = tweet.dislikes.toString()
        holder.comments.text = tweet.comments.count().toString()

        val user = FirebaseAuth.getInstance().currentUser!!
        val authorRef = FirebaseDatabase.getInstance().reference.child("users").child(tweet.author!!).child("name")
        authorRef.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                holder.tweetAuthor.text = dataSnapshot.value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        val tweetRef = FirebaseDatabase.getInstance().reference.child("tweets").child(tweet.uid!!)


        holder.tweetAuthor.setOnClickListener{
            val intent = Intent(mContext, ProfileActivity::class.java)
            intent.putExtra("uid", tweet.author)
            mContext.startActivity(intent)
        }

        holder.tweetText.setOnClickListener{
            //val intent = Intent(mContext, MenuActivity::class.java)
            //intent.putExtra("topic", topic)
            //mContext.startActivity(intent)
        }

        holder.like.setOnClickListener{
            tweetRef.runTransaction(object: Transaction.Handler{
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val t = mutableData.getValue(TweetModel::class.java)
                        ?: return Transaction.success(mutableData)

                    if (t.likeUsers.containsKey(user.uid)){
                        t.likes = t.likes - 1
                        t.likeUsers.remove(user.uid)
                    }else{
                        t.likes = t.likes + 1
                        t.likeUsers[user.uid] = true
                        if (t.dislikeUsers.containsKey(user.uid)){
                            t.dislikes = t.dislikes - 1
                            t.dislikeUsers.remove(user.uid)
                        }
                    }

                    mutableData.value = t
                    return Transaction.success(mutableData)
                }

                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                }
            })
        }

        holder.dislike.setOnClickListener{
            tweetRef.runTransaction(object: Transaction.Handler{
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val t = mutableData.getValue(TweetModel::class.java)
                        ?: return Transaction.success(mutableData)

                    if (t.dislikeUsers.containsKey(user.uid)){
                        t.dislikes = t.dislikes - 1
                        t.dislikeUsers.remove(user.uid)
                    }else{
                        t.dislikes = t.dislikes + 1
                        t.dislikeUsers[user.uid] = true
                        if (t.likeUsers.containsKey(user.uid)){
                            t.likes = t.likes - 1
                            t.likeUsers.remove(user.uid)
                        }
                    }

                    mutableData.value = t
                    return Transaction.success(mutableData)
                }

                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {

                }
            })
        }

        holder.border.setOnClickListener {
            val intent = Intent(mContext, PostActivity::class.java)
            intent.putExtra("tweet", tweet.uid)
            mContext.startActivity(intent)
        }

    }

    class TweetsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
//TODO: сделать чекбоксы вместо imagebuttonov и заменить уже картинку на аватар
        val tweetText: TextView = itemView.findViewById(R.id.tweet_text)
        val tweetAuthor: TextView = itemView.findViewById(R.id.tweet_author)
        val tweetLikes: TextView = itemView.findViewById(R.id.tweet_likes)
        val tweetDislikes: TextView = itemView.findViewById(R.id.tweet_dislikes)
        val like: ImageButton = itemView.findViewById(R.id.like)
        val dislike: ImageButton = itemView.findViewById(R.id.dislike)
        val border: LinearLayout = itemView.findViewById(R.id.tweet_layout)
        val comments: TextView = itemView.findViewById(R.id.tweet_comments)

    }
}