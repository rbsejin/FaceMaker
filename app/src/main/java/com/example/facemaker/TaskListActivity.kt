package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*

const val TASK_ID = "task id"

class TaskListActivity : AppCompatActivity() {
    private val newTaskActivityRequestCode = 1
    private val taskDetailRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        val recyclerView = findViewById<RecyclerView>(R.id.task_recycler_view)
        recyclerView.adapter = TaskAdapter { task -> adapterOnClick(task) }

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            fabOnClick()
        }
    }

    private fun fabOnClick() {
        val intent = Intent(this, AddTaskActivity::class.java)
        startActivityForResult(intent, newTaskActivityRequestCode)
    }

    /* Opens FlowerDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(task: Task) {
        val intent = Intent(this, TaskDetailActivity()::class.java)
        intent.putExtra(TASK_ID, task.id)
        //startActivity(intent)
        startActivityForResult(intent, taskDetailRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newTaskActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let { data ->
                val content = data.getStringExtra(TASK_CONTENT)
                content?.let {
                    val lastId = TaskManager.getLastId()
                    val id = if (lastId == null) 0L else lastId + 1
                    val task = Task(id, content, Calendar.getInstance().time)
                    TaskManager.addTask(task)
                }
            }
        } else if (requestCode == taskDetailRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let { data ->
                val id = data.getLongExtra(REMOVED_TASK_ID, 0)
                TaskManager.removeTaskForId(id)
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.task_recycler_view)
        (recyclerView.adapter as TaskAdapter).notifyDataSetChanged()
    }
}