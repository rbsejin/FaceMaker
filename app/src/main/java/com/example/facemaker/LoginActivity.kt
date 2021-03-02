package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.facemaker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.apply {
            emailLoginButton.setOnClickListener {
                moveToMainPage()
            }

            googleSignInButton.setOnClickListener {
                moveToMainPage()
            }

            facebookLoginButton.setOnClickListener {
                moveToMainPage()
            }

            twitterLoginButton.setOnClickListener {
                moveToMainPage()
            }
        }
    }

    private fun moveToMainPage() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}