package com.example.facemaker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        var currentTaskId: Long? = null

        /* Connect variables to UI elements. */
        val taskContent: TextView = findViewById(R.id.task_detail_content)

        val bundle: Bundle? = intent.extras
        currentTaskId = bundle?.getLong(TASK_ID)

        currentTaskId?.let {
            val currentTask = TaskManager.getTaskForId(it)
            taskContent.text = currentTask?.content
        }
    }
}
