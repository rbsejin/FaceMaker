package com.example.facemaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class DisplayTasksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_tasks_layout)

        val taskList = DataSource(this).getTaskList()
        val recyclerView = findViewById<RecyclerView>(R.id.task_recycler_view)
        recyclerView.adapter = TaskAdapter(taskList)
    }

    fun addTask(view: View) {
        Toast.makeText(this, "버튼을 클릭하였습니다.", Toast.LENGTH_SHORT).show()
    }
}