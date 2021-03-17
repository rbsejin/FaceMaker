package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.facemaker.game.GameActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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
                startActivity(Intent(this, LoginActivity()::class.java))
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
                finish()
            }
    }

    private fun playGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private var smartListMap = mutableMapOf<String, Boolean>()
        private var generalMap = mutableMapOf<String, Boolean>()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val account: Preference = findPreference<Preference>("account_info") ?: return
            account.title = Firebase.auth.currentUser.displayName
            account.summary = Firebase.auth.currentUser.email

            Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/smartList")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue<Map<String, Boolean>>()?.let {
                            smartListMap = it as MutableMap<String, Boolean>
                        }

                        for (smartList in smartListMap) {
                            when (smartList.key) {
                                "all" -> {
                                    val preference = findPreference<SwitchPreferenceCompat>("all")!!
                                    preference.isChecked = smartListMap["all"] ?: true
                                }
                                "important" -> {
                                    val preference =
                                        findPreference<SwitchPreferenceCompat>("important")!!
                                    preference.isChecked = smartListMap["important"] ?: true
                                }
                                "planned" -> {
                                    val preference =
                                        findPreference<SwitchPreferenceCompat>("planned")!!
                                    preference.isChecked = smartListMap["planned"] ?: true
                                }
                                "completed" -> {
                                    val preference =
                                        findPreference<SwitchPreferenceCompat>("completed")!!
                                    preference.isChecked = smartListMap["completed"] ?: true
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/general")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue<Map<String, Boolean>>()?.let {
                            smartListMap = it as MutableMap<String, Boolean>
                        }

                        for (smartList in smartListMap) {
                            when (smartList.key) {
                                "new_task_add_top" -> {
                                    val preference =
                                        findPreference<SwitchPreferenceCompat>("new_task_add_top")!!
                                    preference.isChecked = smartListMap["new_task_add_top"] ?: true
                                }
                                "important_task_move_top" -> {
                                    val preference =
                                        findPreference<SwitchPreferenceCompat>("important_task_move_top")!!
                                    preference.isChecked =
                                        smartListMap["important_task_move_top"] ?: true
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
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
                "all" -> {
                    val preference = findPreference<SwitchPreferenceCompat>("all")!!

                    smartListMap["all"] = preference.isChecked

                    Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/smartList/all")
                        .setValue(preference.isChecked)
                }
                "important" -> {
                    val preference = findPreference<SwitchPreferenceCompat>("important")!!

                    smartListMap["important"] = preference.isChecked

                    Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/smartList/important")
                        .setValue(preference.isChecked)
                }
                "planned" -> {
                    val preference = findPreference<SwitchPreferenceCompat>("planned")!!

                    smartListMap["planned"] = preference.isChecked

                    Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/smartList/planned")
                        .setValue(preference.isChecked)
                }
                "completed" -> {
                    val preference = findPreference<SwitchPreferenceCompat>("completed")!!

                    smartListMap["completed"] = preference.isChecked

                    Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/smartList/completed")
                        .setValue(preference.isChecked)
                }
                "new_task_add_top" -> {
                    val preference = findPreference<SwitchPreferenceCompat>("new_task_add_top")!!

                    generalMap["new_task_add_top"] = preference.isChecked

                    Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/general/new_task_add_top")
                        .setValue(preference.isChecked)
                }
                "important_task_move_top" -> {
                    val preference =
                        findPreference<SwitchPreferenceCompat>("important_task_move_top")!!

                    generalMap["important_task_move_top"] = preference.isChecked

                    Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/general/important_task_move_top")
                        .setValue(preference.isChecked)
                }
                "completion_sound" -> {
                    val preference =
                        findPreference<SwitchPreferenceCompat>("completion_sound")!!

                    generalMap["completion_sound"] = preference.isChecked

                    Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/general/completion_sound")
                        .setValue(preference.isChecked)
                }
            }

            return super.onPreferenceTreeClick(preference)
        }
    }
}