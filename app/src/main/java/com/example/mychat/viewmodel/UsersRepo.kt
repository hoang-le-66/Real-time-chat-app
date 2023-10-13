package com.example.mychat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mychat.Utils
import com.example.mychat.model.Users
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepo {
    private var firestore = FirebaseFirestore.getInstance()
    //Get the list of user using LiveData
    fun getUsers(): LiveData<List<Users>>{
        val users = MutableLiveData<List<Users>>()

        firestore.collection("Users").addSnapshotListener{snapshot, exception->

            if(exception!=null){
                return@addSnapshotListener
            }

            val usersList = mutableListOf<Users>()
            snapshot?.documents?.forEach { document ->

                val user = document.toObject(Users::class.java)
                //all the users who are not same as the user logged in
                //add them to the list
                if(user!!.userid !=Utils.getUILoggedIn()){
                    user.let {
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }

        }
        return users
    }
}