package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

const val TASK_CONTENT = "content"

class AddTaskActivity : AppCompatActivity() {
    private lateinit var addTaskContent: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        findViewById<Button>(R.id.add_task_button).setOnClickListener {
            addTask()
        }
        addTaskContent = findViewById(R.id.add_task_content)
    }

    private fun addTask() {
        val resultIntent = Intent()

        if (addTaskContent.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val content = addTaskContent.text.toString()
            resultIntent.putExtra(TASK_CONTENT, content)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}