package com.example.mychat.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.mychat.activity.MainActivity
import com.example.mychat.R
import com.example.mychat.model.SharedPrefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {
    companion object {
        private const val KEY_REPLY_TEXT = "KEY_REPLY_TEXT"

        val sharedPrefs: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPrefs?.getString("token","")
            }
        set(value) {
            sharedPrefs?.edit()?.putString("token",value)?.apply()
        }

    }

    override fun onNewToken(newtoken: String) {
        super.onNewToken(newtoken)
        token = newtoken
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        //For replying from the noti
        val remoteInput = RemoteInput.Builder(KEY_REPLY_TEXT).setLabel("Reply").build()

        val replyIntent = Intent(this, NotificationReply::class.java)

        //Create an intent for reply action
        val replyPendingIntent = PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_MUTABLE)

        val replyAction = NotificationCompat.Action.Builder(R.drawable.reply, "Reply", replyPendingIntent)
            .addRemoteInput(remoteInput).build()

        val sharedCustomerPref = SharedPrefs(applicationContext)
        //if we have the notification id then we can use that id and send a reply on it
        sharedCustomerPref.setIntValue("values", notificationID)

        val notification= NotificationCompat.Builder(this, CHANNEL_ID).
                setContentText(Html.fromHtml("<b>${message.data["title"]}</b>:${message.data["message"]}"))
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(replyAction)
            .build()

        notificationManager.notify(notificationID, notification)




    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {

            description= "My Channel Description"
            enableLights(true)
            lightColor = Color.GREEN

        }
        notificationManager.createNotificationChannel(channel)

    }
}