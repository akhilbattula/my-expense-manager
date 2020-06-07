package com.akhil.myexpensemanager.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.akhil.myexpensemanager.AppConstants
import com.akhil.myexpensemanager.R
import com.akhil.myexpensemanager.models.ModelClass
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_account.*
import kotlinx.android.synthetic.main.activity_profile_filling.*
import java.util.*

class AddAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)

        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()

        btn_account_save.setOnClickListener {

            if(et_account_name.text.isEmpty()){
                Toast.makeText(this@AddAccountActivity,"Please Enter Account Name",Toast.LENGTH_SHORT).show()
            }else if(et_account_description.text.isEmpty()){
                Toast.makeText(this@AddAccountActivity,"Please Enter Account Description", Toast.LENGTH_SHORT).show()
            }else{
                val date= Date()
                Log.w("akhilll",""+date)
                Log.w("akhilll","Email: "+auth.currentUser?.email.toString())
                val email: String? = getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE).getString("email","abc@gmail.com")
                val id = email?.substring(0,email.indexOf("@"))+"-account-"+date.time
                Log.w("akhilll","id: "+id)

                val account = ModelClass.Account(id,et_account_name.text.toString(),et_account_description.text.toString(),"Open",date.toString(),date.toString())
                db.collection("users")
                    .document(auth.currentUser?.email.toString())
                    .collection("accounts")
                    .document(id)
                    .set(account)
                    .addOnSuccessListener {
                        Log.d(AppConstants.TAG, "DocumentSnapshot added")
                        val intent = Intent()
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e(AppConstants.TAG, "Error adding document")
                    }
            }

        }


    }

}