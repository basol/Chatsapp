package com.example.chatsapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance()
    }


    fun signUp(view: View) {

        if (signUpPasswordEditText.text.toString() == signUpRePasswordEditText.text.toString()) {
            if (!TextUtils.isEmpty(signUpEmailEditText.text.toString()) && !TextUtils.isEmpty(signUpPasswordEditText.text.toString()) && !TextUtils.isEmpty(signUpRePasswordEditText.text.toString())) {
                mAuth.createUserWithEmailAndPassword(
                    signUpEmailEditText.text.toString(),
                    signUpPasswordEditText.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            val user = mAuth.currentUser

                            Toast.makeText(
                                this, "Authentication successful.",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this, FeedActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this, "Authentication failed.",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
            } else {
                Toast.makeText(this, "Fill the blanks", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show()
        }
    }
}
