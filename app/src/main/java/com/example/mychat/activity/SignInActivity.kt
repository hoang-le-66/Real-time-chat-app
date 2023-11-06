@file:Suppress("DEPRECATION")

package com.example.mychat.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mychat.R
import com.example.mychat.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialogSignIn: ProgressDialog
    private lateinit var signInBinding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

        signInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        auth = FirebaseAuth.getInstance()
        //if user has login, it's only once until you logout
        if (auth.currentUser!=null){

            startActivity(Intent(this, MainActivity::class.java))

        }

        progressDialogSignIn = ProgressDialog(this)

        signInBinding.tvCreateNewAccount.setOnClickListener {

            startActivity(Intent(this, SignUpActivity::class.java))

        }

        signInBinding.btnLogin.setOnClickListener {

            email = signInBinding.edtLoginEmail.text.toString()
            password = signInBinding.edtLoginPassword.text.toString()

            if(signInBinding.edtLoginEmail.text.isEmpty()){
                Toast.makeText(this,"Email can't be empty",Toast.LENGTH_LONG).show()
            }
            if(signInBinding.edtLoginPassword.text.toString().isEmpty()){
                Toast.makeText(this,"Password can't be empty",Toast.LENGTH_LONG).show()
            }
            if(signInBinding.edtLoginEmail.text.isEmpty()){
                Toast.makeText(this,"Email can't be empty",Toast.LENGTH_LONG).show()
            }
            if(signInBinding.edtLoginEmail.text.isEmpty()){
                Toast.makeText(this,"Email can't be empty",Toast.LENGTH_LONG).show()
            }
            signInWithFireBase(password, email)

        }

    }


    private fun signInWithFireBase(password: String, email: String){
        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Signing in")

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {

            if(it.isSuccessful){
                progressDialogSignIn.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                progressDialogSignIn.dismiss()
                Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_LONG).show()
            }

        }.addOnFailureListener { exception ->
            when(exception){

                is FirebaseAuthInvalidCredentialsException->{
                    Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_LONG).show()
                }else->{
                    Toast.makeText(this,"Auth Failed",Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        progressDialogSignIn.dismiss()
        //Finish all of activities in backstack
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialogSignIn.dismiss()

    }


    private fun checkField(): Boolean {
        val email = signInBinding.edtLoginEmail.text.toString()
        val password = signInBinding.edtLoginPassword.text.toString()
        if(email == ""){
            signInBinding.edtLoginEmail.error = "This is required field"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signInBinding.edtLoginEmail.error = "Check email format"
            return false
        }
        if (password == ""){
            signInBinding.edtLoginPassword.error = "This field is required"
            return false
        }
        if(password.length <= 7){
            signInBinding.edtLoginPassword.error = "Password at least 8 char"
            return false
        }
        return true
    }
}