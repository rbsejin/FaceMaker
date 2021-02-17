package com.example.facemaker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*

const val TASK_ID = "task id"
const val REMOVED_PROJECT_ID = "removedTaskId"

class TaskListActivity() : AppCompatActivity() {
    private val newTaskActivityRequestCode = 1
    private val taskDetailRequestCode = 2

    private lateinit var currentProject: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        val bundle: Bundle? = intent.extras
        val currentProjectId: Long = bundle?.getLong(PROJECT_ID) ?: return
        currentProject = ProjectManager.getProjectForId(currentProjectId) ?: return

        val projectContent: TextView = findViewById(R.id.task_list_project_content)
        projectContent.text = currentProject.content

        projectContent.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("프로젝트 이름 바꾸기")
                .setNegativeButton("취소", null)
                .setPositiveButton("저장") { _, _ ->
                    currentProject.content = "바뀐 이름"
                    projectContent.text = currentProject.content
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.task_recycler_view)
        recyclerView.adapter = TaskAdapter(currentProject) { task -> adapterOnClick(task) }

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            fabOnClick()
        }
    }

    private fun fabOnClick() {
        val intent = Intent(this, AddTaskActivity::class.java)
        startActivityForResult(intent, newTaskActivityRequestCode)
    }

    /* Opens TaskDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(task: Task) {
        val intent = Intent(this, TaskDetailActivity()::class.java)
        intent.putExtra(PROJECT_ID, currentProject.id)
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
                    val taskId = currentProject.createId()
                    val task = Task(taskId, currentProject.id, content, Calendar.getInstance().time)
                    currentProject.addTask(task)
                }
            }
        } else if (requestCode == taskDetailRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let { data ->
                val id = data.getLongExtra(REMOVED_PROJECT_ID, 0)
                currentProject.removeTaskForId(id)
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.task_recycler_view)
        (recyclerView.adapter as TaskAdapter).notifyDataSetChanged()
    }
}