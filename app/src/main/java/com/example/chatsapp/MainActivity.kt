package com.example.chatsapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)


        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signUp(view: View){

        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun signIn(view: View){

        signInButton.visibility = View.INVISIBLE
        signInProgressBar.visibility = View.VISIBLE

        if(!TextUtils.isEmpty(emailEditText.text.toString()) && !TextUtils.isEmpty(passwordEditText.text.toString())) {
            mAuth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser

                        val intent = Intent(this, FeedActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        signInButton.visibility = View.VISIBLE
                        signInProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }else {
            signInButton.visibility = View.VISIBLE
            signInProgressBar.visibility = View.INVISIBLE
            Toast.makeText(
                this, "Fill the blanks.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun forgotPassword(view: View) {

        //intent
    }
}
