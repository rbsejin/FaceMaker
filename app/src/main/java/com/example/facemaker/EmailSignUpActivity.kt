//package com.example.facemaker
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.facemaker.databinding.ActivityEmailSignUpBinding
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//
//class EmailSignUpActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityEmailSignUpBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_email_sign_up)
//
//        binding = ActivityEmailSignUpBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val signUpCompleteButton: Button = findViewById(R.id.sign_up_complete_button)
//        signUpCompleteButton.setOnClickListener {
//            val email: String = binding.editTextEmailAddress.text.toString()
//            val password: String = binding.editTextPassword.text.toString()
//            val confirmPassword: String = binding.editTextConfirmPassword.text.toString()
//            val userName: String = binding.editTextUserName.text.toString()
//
//            if (email.isEmpty()) {
//                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            if (password.isEmpty()) {
//                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            if (password != confirmPassword) {
//                Toast.makeText(this, "비밀번호가 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            if (userName.isEmpty()) {
//                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            createUser(email, password, userName)
//        }
//    }
//
//    private fun createUser(email: String, password: String, userName: String) {
//        Firebase.auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("login", "createUserWithEmail:success")
//
//                    val resultIntent = Intent()
//                    resultIntent.putExtra(SIGN_UP_EMAIL, email)
//                    resultIntent.putExtra(SIGN_UP_PASSWORD, password)
//                    resultIntent.putExtra(SIGN_UP_NICKNAME, userName)
//                    setResult(Activity.RESULT_OK, resultIntent)
//                    finish()
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("Login", "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//    }
//}