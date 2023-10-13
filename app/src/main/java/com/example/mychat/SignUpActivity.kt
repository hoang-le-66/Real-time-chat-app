@file:Suppress("DEPRECATION")

package com.example.mychat

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mychat.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var signUpAuth: FirebaseAuth
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var progressDialogSignUp: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        fireStore = FirebaseFirestore.getInstance()
        signUpAuth = FirebaseAuth.getInstance()
        progressDialogSignUp = ProgressDialog(this)

        signUpBinding.tvSignUpToLogin.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))

        }

        signUpBinding.btnSignUp.setOnClickListener {
            name = signUpBinding.edtUserName.text.toString()
            email = signUpBinding.edtSignUpEmail.text.toString()
            password = signUpBinding.edtSignUpPassword.text.toString()

            if (signUpBinding.edtUserName.text.isEmpty()){
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show()
            }

            if (signUpBinding.edtSignUpEmail.text.isEmpty()){
                Toast.makeText(this, "Email can't be empty", Toast.LENGTH_SHORT).show()
            }

            if (signUpBinding.edtSignUpPassword.text.isEmpty()){
                Toast.makeText(this, "Password can't be empty", Toast.LENGTH_SHORT).show()
            }

            if (signUpBinding.edtUserName.text.isNotEmpty() && signUpBinding.edtSignUpEmail.text.isNotEmpty() && signUpBinding.edtSignUpPassword.text.isNotEmpty()){
                createAnAccount(name, password, email)
            }

        }
    }

    private fun createAnAccount(name: String, password: String, email: String) {
        progressDialogSignUp.show()
        progressDialogSignUp.setMessage("Registering User")

        signUpAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
            if (task.isSuccessful){
                val user = signUpAuth.currentUser
                val dataHashMap = hashMapOf("userid" to user!!.uid!!, "username" to name, "useremail" to email, "status" to "default",
                    "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png")

                fireStore.collection("Users").document(user.uid).set(dataHashMap)

                progressDialogSignUp.dismiss()
                startActivity(Intent(this, SignInActivity::class.java))
            }

        }

    }
}