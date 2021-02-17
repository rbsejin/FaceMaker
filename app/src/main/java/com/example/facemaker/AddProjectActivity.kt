package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

const val PROJECT_CONTENT = "project content"

class AddProjectActivity : AppCompatActivity() {
    private lateinit var addProjectContent: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)

        findViewById<Button>(R.id.add_project_button).setOnClickListener {
            addProject()
        }
        addProjectContent = findViewById(R.id.add_project_content)
    }

    private fun addProject() {
        val resultIntent = Intent()

        if (addProjectContent.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val content = addProjectContent.text.toString()
            resultIntent.putExtra(PROJECT_CONTENT, content)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}