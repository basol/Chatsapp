package com.example.chatsapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_feed.*
import java.util.*
import kotlin.collections.ArrayList


class FeedActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var recyclerViewAdapter: RecyclerViewAdapter

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    var chatMessagesList: ArrayList<String> = ArrayList()

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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        chatMessagesList.add("hello world")
//        chatMessagesList.add("test strings")

        val recyclerViewManager = LinearLayoutManager(applicationContext)

        feedRecyclerView.layoutManager = recyclerViewManager
        feedRecyclerView.itemAnimator = DefaultItemAnimator()

        feedRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        recyclerViewAdapter = RecyclerViewAdapter(chatMessagesList)
        feedRecyclerView.adapter = recyclerViewAdapter

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        getMessages()

        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()


        OneSignal.idsAvailable { userId, registrationId ->
            databaseReference.child("Profiles").child(user?.uid!!).child("PlayerID").setValue(userId)
        }



        chatEditText.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage(chatEditText)
            }
            false
        }
    }

//    fun openSoftKeyboard(context: Context, view: View) {
//        view.requestFocus()
//        // open the soft keyboard
//        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
//    }

    fun sendMessage(view: View) {

        val messageToSend = chatEditText.text.toString()

        if (!TextUtils.isEmpty(messageToSend)) {

            val user = mAuth.currentUser
            val currentTime = Calendar.getInstance().time
            val uuid = UUID.randomUUID().toString()

            databaseReference.child("Chats").child(uuid).child("email").setValue(user?.email!!)
            databaseReference.child("Chats").child(uuid).child("time").setValue(ServerValue.TIMESTAMP)
            databaseReference.child("Chats").child(uuid).child("message").setValue(messageToSend)

            chatEditText.setText("")
        }

//        openSoftKeyboard(this, view)
    }


    private fun getMessages(){

        val reference = firebaseDatabase.getReference("Chats")

        val query = reference.orderByChild("time")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                chatMessagesList.clear()

                for (ds in dataSnapshot.children){

                    val hashMap = ds.value as HashMap<*, *>
                    chatMessagesList.add(hashMap["email"].toString() + ": " + hashMap["message"].toString())

                    recyclerViewAdapter.notifyDataSetChanged()

                }

            }

        })

    }

}
