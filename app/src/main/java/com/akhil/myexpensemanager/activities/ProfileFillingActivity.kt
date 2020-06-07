package com.akhil.myexpensemanager.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.akhil.myexpensemanager.AppConstants
import com.akhil.myexpensemanager.R
import com.akhil.myexpensemanager.models.ModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_filling.*

class ProfileFillingActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_filling)

        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()

        sharedPreferences = this.getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        et_email.setText(auth.currentUser?.email)
        et_email.isEnabled = false

        et_phonenumber.setText(auth.currentUser?.phoneNumber)

        btn_save.setOnClickListener {

            if(et_fname.text.isEmpty() || et_lname.text.isEmpty() || et_phonenumber.text.isEmpty() || et_email.text.isEmpty()){
                Toast.makeText(this@ProfileFillingActivity,"All fields are mandatory",Toast.LENGTH_SHORT).show()
            }else if(!Patterns.EMAIL_ADDRESS.matcher(et_email.text).matches()){
                Toast.makeText(this@ProfileFillingActivity,"Please Enter Valid Email",Toast.LENGTH_SHORT).show()
            }else{
                val user = ModelClass.User(et_fname.text.toString(),et_lname.text.toString(),et_phonenumber.text.toString(),et_email.text.toString())
                db.collection("users")
                    .document(auth.currentUser?.email.toString())
                    .set(user)
                    .addOnSuccessListener {
                        Log.d(AppConstants.TAG, "DocumentSnapshot added")
                        editor =  sharedPreferences.edit()
                        editor.putBoolean("isProfileFilled",true)
                        editor.apply()
                        val intent = Intent(this@ProfileFillingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@ProfileFillingActivity,"Profile saved successfully",Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener {
                        Log.e(AppConstants.TAG, "Error adding document")
                    }
            }
        }
    }
}
