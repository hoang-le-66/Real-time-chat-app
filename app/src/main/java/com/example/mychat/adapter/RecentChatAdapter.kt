package com.example.mychat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.model.RecentChats
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter:RecyclerView.Adapter<RecentChatHolder>() {
    private var listOfChats = listOf<RecentChats>()
    private var listener: onRecentChatClicked? = null
    private var recentModel = RecentChats()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist, parent, false)
        return RecentChatHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }

    override fun onBindViewHolder(holder: RecentChatHolder, position: Int) {
        val recentChatList = listOfChats[position]
        recentModel= recentChatList
        holder.userName.setText(recentChatList.name)

        val theMessage= recentChatList.message!!.split("").take(4).joinToString ("")
        //??
        val makeLastMessage= "${recentChatList.person}: ${theMessage}"
        holder.lastMessage.setText(makeLastMessage)
        Glide.with(holder.itemView.context).load(recentChatList.friendsimage).into(holder.imageView)

        holder.timeView.setText(recentChatList.time!!.substring(0,5))

        holder.itemView.setOnClickListener {
            listener?.getOnRecentChatClicked(position,recentChatList)
        }

    }

    fun setOnResentChatListener(listener: onRecentChatClicked){
        this.listener= listener
    }

    fun setOnRecentList(list: List<RecentChats>){
        this.listOfChats= list
    }
}

class RecentChatHolder(itemview: View): RecyclerView.ViewHolder(itemview){

    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)

}

interface onRecentChatClicked{
    fun getOnRecentChatClicked(position: Int, recentChatList: RecentChats)
}