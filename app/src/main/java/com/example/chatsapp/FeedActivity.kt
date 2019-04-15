package com.example.chatsapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    var chatMessagesList: ArrayList<String>

    init {
        chatMessagesList = ArrayList<String>()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.feed_option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.option_menu_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.option_menu_sign_out -> {
                mAuth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        chatMessagesList.add("hello world")
        chatMessagesList.add("test strings")

        recyclerViewAdapter = RecyclerViewAdapter(chatMessagesList)
        feedRecyclerView.adapter = recyclerViewAdapter

        mAuth = FirebaseAuth.getInstance()
    }

    fun sendMessage(view: View) {

    }
}
