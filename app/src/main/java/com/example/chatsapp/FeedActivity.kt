package com.example.chatsapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_feed.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class FeedActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var user: FirebaseUser
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    var targetUserID = ""
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
            R.id.option_menu_new_chat -> {
                val intent = Intent(this,  UserListActivity::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val recyclerViewManager = LinearLayoutManager(applicationContext)

        feedRecyclerView.layoutManager = recyclerViewManager
        feedRecyclerView.itemAnimator = DefaultItemAnimator()

        feedRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        recyclerViewAdapter = RecyclerViewAdapter(chatMessagesList)
        feedRecyclerView.adapter = recyclerViewAdapter

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        user = mAuth.currentUser!!

        if(!TextUtils.isEmpty(intent.getStringExtra("targetUserID"))){
            targetUserID = intent.getStringExtra("targetUserID")
        }

        getMessages()

        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
//        OneSignal.idsAvailable { userId, registrationId ->
//            databaseReference.child("Profiles").child(user.uid).child("PlayerID").setValue(userId)
//        }
        chatEditText.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage(chatEditText)
            }
            false
        }
    }



    fun sendMessage(view: View) {

        val messageToSend = chatEditText.text.toString()
        var playerIDtoSendNotification = ""

        if (!TextUtils.isEmpty(messageToSend)) {

            val uuid = UUID.randomUUID().toString()

            val ordered = mutableListOf(user.uid, targetUserID)
            ordered.sort()

            val newPath = ordered[0]+"-"+ordered[1]
            databaseReference.child("Chats").child(newPath).child(uuid).child("message").setValue(messageToSend)
            databaseReference.child("Chats").child(newPath).child(uuid).child("email").setValue(user.email)
            databaseReference.child("Chats").child(newPath).child(uuid).child("time").setValue(ServerValue.TIMESTAMP)

            chatEditText.setText("")

            val ref = FirebaseDatabase.getInstance().getReference("Profiles")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (ds in dataSnapshot.children) {

                        val hashMap = ds.value as HashMap<*, *>
//                        Log.e("userid", ds.key)
//                        Log.e("ttarget", targetUserID)
                        if (ds.key == targetUserID) {
//                            Log.e("inside of if block", ds.key)
                            playerIDtoSendNotification = hashMap["PlayerID"].toString()
//                            Log.e("playerid in fun", "heh$playerIDtoSendNotification")
                        }
                    }

                }
            })
            Log.e("player id", playerIDtoSendNotification)
            try {
                OneSignal.postNotification(JSONObject("{'contents': {'en':'$messageToSend'}, 'include_player_ids': ['$playerIDtoSendNotification']}"),
                    object : OneSignal.PostNotificationResponseHandler {
                        override fun onSuccess(response: JSONObject) {
                            Log.i("OneSignalExample", "postNotification Success: $response")
                        }

                        override fun onFailure(response: JSONObject) {
                            Log.e("OneSignalExample", "postNotification Failure: $response")
                        }
                    })
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            getMessages()
        }
    }

    private fun getMessages(){

        val ordered = mutableListOf(user.uid, targetUserID)
        ordered.sort()
        val newPath = ordered[0]+"-"+ordered[1]
        val reference = firebaseDatabase.getReference("Chats/$newPath")
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
        recyclerViewAdapter.notifyItemRangeInserted(recyclerViewAdapter.itemCount, chatMessagesList.size - 1)
    }
}
