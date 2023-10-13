package com.example.mychat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mychat.Utils
import com.example.mychat.model.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getMessages(friendID: String) : LiveData<List<Messages>>{
        //Create a livedata which contain list of message
        val messages= MutableLiveData<List<Messages>>()
        //no matter who is sender or receiver
        val uniqueID= listOf(Utils.getUILoggedIn(),friendID).sorted()
        uniqueID.joinToString(separator = "")
        
        //query to firebase firestore
        firestore.collection("Messages")
            .document(uniqueID.toString())
            .collection("chats")
            .orderBy("time",Query.Direction.ASCENDING).addSnapshotListener { value, error ->
                //value represents to snapshot
                if (error!=null){
                    return@addSnapshotListener
                }
                val messageList = mutableListOf<Messages>()
                if(!value!!.isEmpty){
                    value.documents.forEach{document->
                        //Sender is logged-User, receiver is friend and reserve

                        val messageModel = document.toObject(Messages::class.java)
                        if (messageModel!!.sender.equals(Utils.getUILoggedIn()) && messageModel.receiver.equals(friendID) ||
                            messageModel!!.sender.equals(friendID) && messageModel.receiver.equals(Utils.getUILoggedIn())){

                            messageModel.let {
                                messageList.add(it!!)
                            }

                        }

                    }
                    messages.value= messageList

                }
            }
        return messages
    }

}