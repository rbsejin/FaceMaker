package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.facemaker.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk

const val RC_SIGN_IN = 1
const val RC_SIGN_OUT = 2

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    // GoogleLogin 관리 클래스
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            updateUI(null)
        }

        // kakao
        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
        // update kakao login UI

        binding.apply {
            emailLoginButton.setOnClickListener {
                updateUI(null)
            }

            googleSignInButton.setOnClickListener {
                googleLogin()
            }

            facebookLoginButton.setOnClickListener {
                updateUI(null)
            }

            twitterLoginButton.setOnClickListener {
                updateUI(null)
            }

            kakaoSignInButton.setOnClickListener {
                // 로그인 공통 callback 구성
//                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//                    if (error != null) {
//                        Log.e("login", "로그인 실패", error)
//                    } else if (token != null) {
//                        Log.i("login", "로그인 성공 ${token.accessToken}")
//                        updateUI(null)
//                    }
//                }
//
//                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
//                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
//                    UserApiClient.instance.loginWithKakaoTalk(
//                        this@LoginActivity,
//                        callback = callback
//                    )
//                } else {
//                    UserApiClient.instance.loginWithKakaoAccount(
//                        this@LoginActivity,
//                        callback = callback
//                    )
//                }
            }
        }
    }

    private fun googleLogin() {
        binding.loading.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            //val task = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                //val account = task!!.signInAccount!!
                Log.d("login", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("login", "Google sign in failed", e)
                // ...
            }
        }
    }

    /* 사용자가 정상적으로 로그인하면 GoogleSignInAccount 객체에서 ID 토큰을 가져와서
     Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase에 인증합니다. */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("login", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("login", "signInWithCredential:failure", task.exception)
                    // ...
                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT)
                        .show()
                    updateUI(null)
                }

                // ...
            }
    }

    /* 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다. */
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        binding.loading.visibility = View.GONE

        currentUser?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}