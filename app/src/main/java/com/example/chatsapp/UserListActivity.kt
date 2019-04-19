package com.example.chatsapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var user: FirebaseUser
    var userList: ArrayList<String> = ArrayList()
    var arrayAdapter: ArrayAdapter<String>? = null
    var targetUserID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        getUserList()

        arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, userList)
        userListListView.adapter = arrayAdapter



        userListListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Unit

                GetID().execute(position.toString())

            }

        OneSignal.idsAvailable { userId, registrationId ->
            databaseReference.child("Profiles").child(user.uid).child("PlayerID").setValue(userId)
        }

    }



    private fun getUserList(){

        databaseReference = firebaseDatabase.getReference("Profiles")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

//                userList.clear()

                for (ds in dataSnapshot.children){

                    val hashMap = ds.value as HashMap<*,*>

                    val userEmail = hashMap["email"].toString()

                    userList.add(userEmail)
                    Log.d("userList ", userList.toString())

                    arrayAdapter?.notifyDataSetChanged()
                }

            }

        })


    }

    inner class GetID: AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {

            val ref = FirebaseDatabase.getInstance().getReference("Profiles")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (ds in dataSnapshot.children) {

                        val hashMap = ds.value as java.util.HashMap<*, *>
                        if (hashMap["email"] == userList[params[0]!!.toInt()]) {
                            targetUserID = ds.key.toString()

                        }
                    }
                }
            })
            Thread.sleep(500)
            return targetUserID
        }

        override fun onPostExecute(result: String?) {
            val intent = Intent(applicationContext, FeedActivity::class.java)
            intent.putExtra("targetUserID", targetUserID)
            startActivity(intent)
        }

    }




}
