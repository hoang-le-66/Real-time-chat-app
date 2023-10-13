package com.example.mychat

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object{
        private val auth =FirebaseAuth.getInstance()
        private var userid: String = ""

        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
//        const val MESSAGE_RIGHT = 1
//        const val MESSAGE_LEFT = 2
//        const val CHANNEL_ID = "com.example.mychat"

        //i can use this method anywhere in this prj, return logged-in userid
        fun getUILoggedIn(): String{
            if(auth.currentUser!=null){
                userid= auth.currentUser!!.uid
            }
            return userid
        }
        fun getTime(): String{
            val formatter = SimpleDateFormat("HH:mm:ss")
            val date: Date = Date(System.currentTimeMillis())
            val stringDate = formatter.format(date)

            return stringDate
        }
    }


}