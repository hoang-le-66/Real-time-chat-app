package com.example.mychat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.model.Users
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter: RecyclerView.Adapter<UserHolder>() {
    //Users from model class
    private var listOfUsers = listOf<Users>()
    private var listener : OnUserClickListener? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.userlistitem,parent,false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val users = listOfUsers[position]
        //I don't want to display the whole name
        val name = users.username!!.split("\\s".toRegex())[0]
        holder.profileName.setText(name)

        //User status
        if(users.status.equals("Online")){
            holder.statusImageView.setImageResource(R.drawable.onlinestatus)

        }else{
            holder.statusImageView.setImageResource(R.drawable.offlinestatus)

        }

        Glide.with(holder.itemView.context).load(users.imageUrl).into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, users)
        }


    }
    // update the user list and notify the adapter that the data has changed.
    @SuppressLint("NotifyDataSetChanged")
    fun setUserList(list: List<Users>){
        this.listOfUsers = list
        notifyDataSetChanged()
    }

    fun setOnUserClickListener(listener: OnUserClickListener){
        this.listener = listener
    }
}
//Send whole model of selected user
interface OnUserClickListener {
    fun onUserSelected(position: Int,users: Users)
}

class UserHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

    val profileName : TextView = itemView.findViewById(R.id.userName)
    val imageProfile: CircleImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageView: ImageView = itemView.findViewById(R.id.statusOnline)


}