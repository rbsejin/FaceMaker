package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        prefs.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            Timber.i("값 변경 감지 Preference의 key: $key")

            when (key) {
            }
        }
    }

    fun logout() {
        // 로그아웃
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                startActivity(Intent(this, FirebaseUIActivity()::class.java))
                finish()
            }
    }

    fun deleteAccount() {
        // 계정 삭제
        Firebase.database.reference.child("users").child(Firebase.auth.currentUser.uid)
            .removeValue()

        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                startActivity(Intent(this, FirebaseUIActivity()::class.java))
                finish()
            }
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            val settingActivity = (activity as SettingsActivity)

            val key = preference?.key
            Timber.i("클릭 감지 Preference의 key: ${key}")

            when (key) {
                "logout" -> {
                    settingActivity.logout()
                }
                "delete_account" -> {
                    settingActivity.deleteAccount()
                }
            }

            return super.onPreferenceTreeClick(preference)
        }
    }
}