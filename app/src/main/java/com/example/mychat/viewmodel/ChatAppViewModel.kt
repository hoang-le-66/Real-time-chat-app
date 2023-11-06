package com.example.mychat.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychat.MyApplication
import com.example.mychat.model.SharedPrefs
import com.example.mychat.Utils
import com.example.mychat.model.Messages
import com.example.mychat.model.RecentChats
import com.example.mychat.model.Users
import com.example.mychat.notifications.FirebaseService.Companion.token
import com.example.mychat.notifications.entity.NotificationData
import com.example.mychat.notifications.entity.PushNotification
import com.example.mychat.notifications.entity.Token
import com.example.mychat.notifications.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel :ViewModel() {
    val message = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    val usersRepo= UsersRepo()
    val messagesRepo= MessageRepo()
    val recentChatRepo= ChatListRepo()

    var token: String? = null

    init {
        getCurrentUser()
        //??
        getRecentChats()
    }

    //getting all users
    fun getUsers(): LiveData<List<Users>>{
        return usersRepo.getUsers()
    }

    //get current user infomation
    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext

        firestore.collection("Users").document(Utils.getUILoggedIn()).addSnapshotListener{value,error ->
            if (value!!.exists() && value!=null){
                val users = value.toObject(Users::class.java)
                name.value = users?.username!!
                imageUrl.value = users?.imageUrl!!

                val mySharedPrefs = SharedPrefs(context)
                mySharedPrefs.setValue("username", users.username!!)

            }

        }

    }

    //Send message
    //??Coroutines
    fun sendMessage(sender: String, receiver: String, friendName: String, friendImage: String)= viewModelScope.launch {
        val context = MyApplication.instance.applicationContext
        //??
        //MutableLiveData String
        val hashMap = hashMapOf<String, Any>(
            "sender" to sender,
            "receiver" to receiver,
            "message" to message.value!!,
            "time" to Utils.getTime()

        )
        //??
        val uniqueID = listOf(sender,receiver).sorted()
        uniqueID.joinToString(separator = "")

        val friendNameSplit = friendName.split("\\s".toRegex())[0]
        val mySharedPrefs = SharedPrefs(context)
        mySharedPrefs.setValue("friendid", receiver)
        mySharedPrefs.setValue("chatroomid", uniqueID.toString())
        mySharedPrefs.setValue("friendname", friendNameSplit)
        mySharedPrefs.setValue("friendimage", friendImage)
        //??
        //sending message
        firestore.collection("Messages")
            .document(uniqueID.toString())
            .collection("chats")
            .document(Utils.getTime())
            .set(hashMap)
            .addOnCompleteListener { task ->
                //all work for recent chats list
                val hashMapForRecent = hashMapOf<String, Any>(
                    "friendid" to receiver,
                    "time" to Utils.getTime(),
                    "sender" to Utils.getUILoggedIn(),
                    "message" to message.value!!,
                    //fixed here
                    "friendsimage" to friendImage,
                    "name" to friendName,
                    "person" to "you"
                )

                firestore.collection("Conversation${Utils.getUILoggedIn()}")
                    .document(receiver).set(hashMapForRecent)

                firestore.collection("Conversation${receiver}")
                    .document(Utils.getUILoggedIn())
                    .update(
                        "message", message.value!!,
                        "time", Utils.getTime(), "person", name.value!!
                    )
                //for noti work
                //fix path
                firestore.collection("Tokens").document(receiver)
                    .addSnapshotListener { value, error ->
                        if (value != null && value.exists()) {
                            val tokenObject = value.toObject(Token::class.java)

                            token = tokenObject?.token!!

                            val loggedUsername =
                                mySharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]

                            if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {

                                PushNotification(
                                    NotificationData(loggedUsername, message.value!!),
                                    token!!
                                ).also {
                                    sendNotification(it)
                                }
                            } else {
                                Log.e("VIEWMODEL", "NO TOKEN,NO NOTIFICATION")
                                Log.e("ViewModelToken", "tokenis: $token")
                            }

                        }


                        if (task.isSuccessful) {
                            message.value = ""
                        }
                    }
            }
    }

    private fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val respone= RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception){
            Log.e("VIEWMODELERROR", e.toString())

    }


}

    fun getMessages(friendID: String): LiveData<List<Messages>>{

        return messagesRepo.getMessages(friendID)
    }

    fun getRecentChats(): LiveData<List<RecentChats>>{
        return recentChatRepo.getAllChatList()
    }

    fun updateProfile()= viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        val hashMapUser = hashMapOf<String,Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        firestore.collection("Users").document(Utils.getUILoggedIn())
            .update(hashMapUser).addOnCompleteListener { task->
                if(task.isSuccessful){
                    Toast.makeText(context,"UPDATED",Toast.LENGTH_SHORT).show()
                }
            }

        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")
        val hashMapUpdate = hashMapOf<String, Any>("friendsimage" to imageUrl.value!!,
            "name" to name.value!!,
            "person" to name.value!!)
        //??
        if(friendid!=null){

            firestore.collection("Conversation${friendid}")
                .document(Utils.getUILoggedIn()).update(hashMapUpdate)

            firestore.collection("Conversation${Utils.getUILoggedIn()}")
                .document(friendid).update("person","you")
        }

    }

}