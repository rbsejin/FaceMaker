package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.facemaker.data.User
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber

const val RC_SIGN_IN = 123

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setTheme(R.style.LoginUIStyle)
                .setLogo(R.drawable.logo)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    Firebase.database.reference.child("users").child(user.uid).setValue(
                        User(
                            user.uid,
                            user.displayName,
                            user.email,
                            /*user.photoUrl,*/
                            user.isEmailVerified
                        )
                    )
                }

                startActivity(Intent(this, MainActivity()::class.java))
                Timber.i("Successfully signed in")
            } else {
                Timber.i("Sign in failed")
                if (response == null) {
                    finish()
                }

                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    return
                }

                if (response?.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Timber.i(response?.error?.errorCode.toString())
                    return
                }

                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}