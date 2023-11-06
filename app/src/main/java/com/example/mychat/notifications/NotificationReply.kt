package com.example.mychat.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.mychat.R
import com.example.mychat.model.SharedPrefs
import com.example.mychat.Utils
import com.google.firebase.firestore.FirebaseFirestore

//import kotlinx.coroutines.NonCancellable.message


private const val CHANNEL_ID = "my_channel"

class NotificationReply : BroadcastReceiver() {

    val firestore = FirebaseFirestore.getInstance()
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager: NotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val remoteInput = RemoteInput.getResultsFromIntent(intent!!)

        if (remoteInput != null) {

            val repliedText = remoteInput?.getString("KEY_REPLY_TEXT")

            val mysharedPrefs = SharedPrefs(context)
            val friendid = mysharedPrefs.getValue("friendid")
            val chatroomid = mysharedPrefs.getValue("chatroomid")
            val friendname = mysharedPrefs.getValue("friendname")
            val friendimage = mysharedPrefs.getValue("friendimage")

            val hashMap = hashMapOf<String, Any>(
                "sender" to Utils.getUILoggedIn(),
                "time" to Utils.getTime(), "receiver" to friendid!!, "message" to repliedText!!
            )

            //this is for chatroom
            firestore.collection("Messages").document(chatroomid!!)
                .collection("chats").document(Utils.getTime()).set(hashMap)
            //this is for recent chat
            val setHashMap = hashMapOf<String, Any>(
                "friendid" to friendid,
                "time" to Utils.getTime(),
                "sender" to Utils.getUILoggedIn(),
                "message" to repliedText,
                "friendsimage" to friendimage!!,
                "name" to friendname!!,
                "person" to "you",
            )
            firestore.collection("Conversation${Utils.getUILoggedIn()}").document(friendid)
                .set(setHashMap)

            val updateHashMap =
                hashMapOf<String, Any>(
                    "message" to repliedText,
                    "time" to Utils.getTime(),
                    "person" to friendname,
                )


            firestore.collection("Conversation${friendid}").document(Utils.getUILoggedIn())
                .update(updateHashMap)

            val sharedCustomPref = SharedPrefs(context)
            val replyid : Int? = sharedCustomPref?.getIntValue("values",0)


            val repliedNotification  =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.chatapp)
                    .setContentText("Reply Sent").build()

            notificationManager.notify(replyid!!,repliedNotification)

        }

    }


}