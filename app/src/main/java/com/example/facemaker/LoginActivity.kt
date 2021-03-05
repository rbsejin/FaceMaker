//package com.example.facemaker
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.DataBindingUtil
//import com.example.facemaker.databinding.ActivityLoginBinding
//import com.facebook.AccessToken
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.auth.FacebookAuthProvider
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//import com.kakao.sdk.common.KakaoSdk
//import java.util.*
//
//const val RC_SIGN_IN = 1
//const val RC_SIGN_OUT = 2
//const val RC_SIGN_UP = 3
//
//const val SIGN_UP_EMAIL = "sign up email"
//const val SIGN_UP_PASSWORD = "sign up password"
//const val SIGN_UP_NICKNAME = "sign up nickname"
//
//class LoginActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var auth: FirebaseAuth
//    private lateinit var callbackManager: CallbackManager
//
//    // GoogleLogin 관리 클래스
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
//
//        // Initialize Firebase Auth
//        auth = Firebase.auth
//
//        // Configure Google Sign In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//        googleSignInClient.signOut().addOnCompleteListener {
//            updateUI(null)
//        }
//
//        authStateListener = FirebaseAuth.AuthStateListener {
//            val user = it.currentUser
//            if (user != null) {
//                Log.d("TAG", "onAuthStateChanged:signed_in:" + user.uid)
//            } else {
//                Log.d("TAG", "onAuthStateChanged:signed_out")
//            }
//        }
//
//        callbackManager = CallbackManager.Factory.create()
//
//        // kakao
//        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
//        // update kakao login UI
//
//        binding.apply {
//            emailSignUp.setOnClickListener {
//                val intent = Intent(this@LoginActivity, EmailSignUpActivity()::class.java)
//                startActivityForResult(intent, RC_SIGN_UP)
//            }
//
//            emailLoginButton.setOnClickListener {
//                binding.loading.visibility = View.VISIBLE
//
//                val email = binding.emailText.text.toString()
//                val password = binding.passwordText.text.toString()
//                //createUser(email, password)
//
//                if (email.isEmpty()) {
//                    binding.loading.visibility = View.GONE
//                    return@setOnClickListener
//                }
//
//                if (password.isEmpty()) {
//                    binding.loading.visibility = View.GONE
//                    return@setOnClickListener
//                }
//
//                emailLogin(email, password)
//            }
//
//            googleSignInButton.setOnClickListener {
//                googleLogin()
//            }
//
//            facebookLoginButton.setOnClickListener {
//                facebookLogin()
//            }
//
//            twitterLoginButton.setOnClickListener {
//                updateUI(null)
//            }
//
//            kakaoSignInButton.setOnClickListener {
//                // 로그인 공통 callback 구성
////                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
////                    if (error != null) {
////                        Log.e("login", "로그인 실패", error)
////                    } else if (token != null) {
////                        Log.i("login", "로그인 성공 ${token.accessToken}")
////                        updateUI(null)
////                    }
////                }
////
////                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
////                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
////                    UserApiClient.instance.loginWithKakaoTalk(
////                        this@LoginActivity,
////                        callback = callback
////                    )
////                } else {
////                    UserApiClient.instance.loginWithKakaoAccount(
////                        this@LoginActivity,
////                        callback = callback
////                    )
////                }
//            }
//        }
//    }
//
//    private fun emailLogin(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("TAG", "signInWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("TAG", "signInWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    updateUI(null)
//                    // ...
//                }
//
//                // ...
//            }
//    }
//
//    private fun googleLogin() {
//        binding.loading.visibility = View.VISIBLE
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }
//
//    private fun facebookLogin() {
//        LoginManager.getInstance()
//            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
//
//        LoginManager.getInstance()
//            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//                override fun onSuccess(result: LoginResult?) {
//                    handleFacebookAccessToken(result?.accessToken)
//                }
//
//                override fun onCancel() {
//
//                }
//
//                override fun onError(error: FacebookException?) {
//
//                }
//
//            })
//    }
//
//    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
//        var credential = FacebookAuthProvider.getCredential(accessToken?.token!!)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("login", "signInWithCredential:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("login", "signInWithCredential:failure", task.exception)
//                    // ...
//                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT)
//                        .show()
//                    updateUI(null)
//                }
//
//                // ...
//            }
//    }
//
///*    fun generateSSHKey(context: Context){
//        try {
//            val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey = String(Base64.encode(md.digest(), 0))
//                Log.i("AppLog", "key:$hashKey=")
//            }
//        } catch (e: Exception) {
//            Log.e("AppLog", "error:", e)
//        }
//    }*/
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            //val task = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                //val account = task!!.signInAccount!!
//                Log.d("login", "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w("login", "Google sign in failed", e)
//                // ...
//            }
//        } else if (requestCode == RC_SIGN_UP && resultCode == Activity.RESULT_OK) {
//            val email: String = data!!.getStringExtra(SIGN_UP_EMAIL)!!
//            val password: String = data!!.getStringExtra(SIGN_UP_PASSWORD)!!
//            emailLogin(email, password)
//        }
//    }
//
//    /* 사용자가 정상적으로 로그인하면 GoogleSignInAccount 객체에서 ID 토큰을 가져와서
//     Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase에 인증합니다. */
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("login", "signInWithCredential:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("login", "signInWithCredential:failure", task.exception)
//                    // ...
//                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT)
//                        .show()
//                    updateUI(null)
//                }
//
//                // ...
//            }
//    }
//
//    /* 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다. */
//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//
//        auth.addAuthStateListener(authStateListener)
//    }
//
//    override fun onStop() {
//        super.onStop()
//
//        auth.removeAuthStateListener(authStateListener)
//    }
//
//    private fun updateUI(currentUser: FirebaseUser?) {
//        binding.loading.visibility = View.GONE
//
//        currentUser?.let {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
//    }
//}