package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.facemaker.game.GameActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

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

        database = Firebase.database.reference
        auth = Firebase.auth

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
        val userId = auth.currentUser.uid

        // 작업 삭제
        database.child("tasks").get().addOnSuccessListener {
            val ids = mutableListOf<String>()
            for (snapshot in it.children) {
                if (snapshot.child("userId").getValue<String>() == userId) {
                    snapshot.key?.let { id ->
                        ids.add(id)
                    }
                }
            }

            val childUpdates = ids.map { id -> "tasks/$id" to null }.toMap()
            database.updateChildren(childUpdates)
        }

        // 프로젝트 삭제
        database.child("projects").get().addOnSuccessListener {
            val ids = mutableListOf<String>()
            for (snapshot in it.children) {
                if (snapshot.child("hostUserId").getValue<String>() == userId) {
                    snapshot.key?.let { id ->
                        ids.add(id)
                    }
                }
            }

            val childUpdates = ids.map { id -> "projects/$id" to null }.toMap()
            database.updateChildren(childUpdates)
        }

        // 계정 삭제
        database.child("users").child(userId)
            .removeValue()

        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                startActivity(Intent(this, FirebaseUIActivity()::class.java))
                finish()
            }
    }

    private fun playGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val account: Preference = findPreference<Preference>("account_info") ?: return
            account.title = Firebase.auth.currentUser.displayName
            account.summary = Firebase.auth.currentUser.email
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
                "game" -> {
                    settingActivity.playGame()
                }
            }

            return super.onPreferenceTreeClick(preference)
        }
    }
}