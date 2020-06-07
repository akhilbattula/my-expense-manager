package com.akhil.myexpensemanager.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.akhil.myexpensemanager.AppConstants
import com.akhil.myexpensemanager.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "Akhillll"
        private const val RC_SIGN_IN = 1001
    }

    private lateinit var auth: FirebaseAuth
    //private lateinit var binding: ActivityGoogleBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        sharedPreferences = this.getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        google_button.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    editor =  sharedPreferences.edit()
                    editor.putString("email",user?.email)
                    editor.apply()
                    if(sharedPreferences.getBoolean("isProfileFilled",false)){

                        Toast.makeText(this@LoginActivity,"Welcome "+user?.displayName,Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        editor =  sharedPreferences.edit()
                        editor.putBoolean("isProfileFilled",false)
                        editor.putString("email",user?.email)
                        editor.apply()
                        Toast.makeText(this@LoginActivity,"Welcome "+user?.displayName,Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, ProfileFillingActivity::class.java)
                        startActivity(intent)
                        finish()
                    }


                } else {

                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser!=null){
            editor =  sharedPreferences.edit()
            editor.putString("email",currentUser.email)
            editor.apply()
            if(sharedPreferences.getBoolean("isProfileFilled",false)){
                val intent = Intent(this@LoginActivity,
                    MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this@LoginActivity,
                    ProfileFillingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
