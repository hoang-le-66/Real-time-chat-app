package com.example.mychat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mychat.Utils
import com.example.mychat.model.RecentChats
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

//fetch all the messages are present in recent list
class ChatListRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatList(): LiveData<List<RecentChats>>{
        val mainChatList= MutableLiveData<List<RecentChats>>()

        firestore.collection("Conversation${Utils.getUILoggedIn()}")
            .orderBy("time",Query.Direction.DESCENDING)
            .addSnapshotListener{value, error->

                if(error!=null){
                    return@addSnapshotListener
                }
                val chatList = mutableListOf<RecentChats>()
                value?.forEach{document ->
                    val recentModel = document.toObject(RecentChats::class.java)

                    if (recentModel.sender.equals(Utils.getUILoggedIn())){
                        recentModel.let {
                            chatList.add(it)
                        }
                    }

                }

                mainChatList.value = chatList
            }
        return mainChatList
    }

}