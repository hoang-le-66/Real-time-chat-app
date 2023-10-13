@file:Suppress("DEPRECATION")

package com.example.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    var token: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        generateToken()

    }

    private fun generateToken() {
        val firebaseInstance = FirebaseInstallations.getInstance()
        firebaseInstance.id.addOnSuccessListener { installationid ->
            FirebaseMessaging.getInstance().token.addOnSuccessListener { gettoken ->
                token = gettoken

                val hashMap = hashMapOf<String, Any>("token" to token)
                firestore.collection("Tokens").document(Utils.getUILoggedIn()).set(hashMap)
                    .addOnSuccessListener {


                    }

            }

        }
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()

        } else {
            if (navController.currentDestination?.id == R.id.homeFragment) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }

        }
    }

    override fun onPause() {
        super.onPause()

        if (auth.currentUser != null) {


            firestore.collection("Users").document(Utils.getUILoggedIn()).update("status", "Offline")


        }
    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser != null) {


            firestore.collection("Users").document(Utils.getUILoggedIn()).update("status", "Online")


        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {


            firestore.collection("Users").document(Utils.getUILoggedIn()).update("status", "Online")


        }
    }
}