package com.example.facemaker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

const val TASK_ID = "task id"
const val REMOVED_PROJECT_ID = "removedTaskId"

class TaskListActivity() : AppCompatActivity() {
    private val newTaskActivityRequestCode = 1
    private val taskDetailRequestCode = 2

    private lateinit var currentProject: Project
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        val bundle: Bundle? = intent.extras
        val currentProjectId: Int = bundle?.getInt(PROJECT_ID) ?: return
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
                    ProjectManager.save(filesDir)
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.task_recycler_view)
        taskAdapter= TaskAdapter(currentProject) { task -> adapterOnClick(task) }
        recyclerView.adapter = taskAdapter

        // 아이템간 구분선
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // delete to swipe
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                taskAdapter.removeTask(viewHolder.adapterPosition)
            }
        }).apply {
            attachToRecyclerView((recyclerView))
        }

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
                val id = data.getIntExtra(REMOVED_PROJECT_ID, 0)
                currentProject.removeTaskForId(id)
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.task_recycler_view)
        (recyclerView.adapter as TaskAdapter).notifyDataSetChanged()
        ProjectManager.save(filesDir)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.project_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.project_delete_item -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setMessage("삭제하시겠습니까?")
                    .setTitle("계속할까요?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("삭제") { _, _ ->
                        val resultIntent = Intent()
                        resultIntent.putExtra(REMOVED_PROJECT_ID, currentProject.id)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        ProjectManager.save(filesDir)
        super.onStop()
    }
}