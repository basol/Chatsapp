package com.example.chatsapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ProfileActivity : AppCompatActivity() {

    lateinit var selectedImageUri: Uri

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var storageReference: StorageReference
    lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()
        storageReference = FirebaseStorage.getInstance().reference
    }


    @SuppressLint("ObsoleteSdkInt")
    fun selectImage(view: View) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }
        }else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){

            selectedImageUri = data.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                profileImageView.setImageBitmap(bitmap)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    fun saveChanges(view: View) {

        val uuid = UUID.randomUUID().toString()

        val user = mAuth.currentUser
        val imageName = "images/"+user?.uid+".jpg"

        val newRef = storageReference.child(imageName)

        newRef.putFile(selectedImageUri).addOnSuccessListener {

            val downRef = FirebaseStorage.getInstance().getReference(imageName)

            downRef.downloadUrl.addOnSuccessListener {

                val downloadURL = it.toString()

                databaseReference.child("Profiles").child(user?.uid!!).child("email").setValue(user.email)
                databaseReference.child("Profiles").child(user.uid).child("nickname").setValue(profileEditText.text.toString().trim())
                databaseReference.child("Profiles").child(user.uid).child("imageurl").setValue(downloadURL)


                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)

            }

        }.addOnFailureListener {
            Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

}

