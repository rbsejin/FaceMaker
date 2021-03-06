//package com.example.facemaker
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.textfield.TextInputEditText
//
//const val PROJECT_NAME = "project name"
//
//class AddProjectActivity : AppCompatActivity() {
//    private lateinit var addProjectName: TextInputEditText
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.dialog_add_project)
//
//        findViewById<Button>(R.id.add_project_button).setOnClickListener {
//            addProject()
//        }
//        addProjectName = findViewById(R.id.add_project_name)
//    }
//
//    private fun addProject() {
//        val resultIntent = Intent()
//
//        if (addProjectName.text.isNullOrEmpty()) {
//            setResult(Activity.RESULT_CANCELED, resultIntent)
//        } else {
//            val name = addProjectName.text.toString()
//            resultIntent.putExtra(PROJECT_NAME, name)
//            setResult(Activity.RESULT_OK, resultIntent)
//        }
//        finish()
//    }
//}