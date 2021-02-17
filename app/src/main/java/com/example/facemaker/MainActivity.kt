package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*

const val EXTRA_MESSAGE = "com.example.facemaker.MESSAGE"
const val PROJECT_ID = "project id"

class MainActivity : AppCompatActivity() {
    private val projectDetailRequestCode = 1
    private val newProjectActivityRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.project_list_recycler_view)
        recyclerView.adapter = ProjectAdapter { project -> adapterOnClick(project) }

        val bottomButton: View = findViewById(R.id.project_bottom)
        bottomButton.setOnClickListener {
            addButtonOnClick()
        }
    }

    /* Opens ProjectDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(project: Project) {
        val intent = Intent(this, TaskListActivity()::class.java)
        intent.putExtra(PROJECT_ID, project.id)
        startActivityForResult(intent, projectDetailRequestCode)
    }

    private fun addButtonOnClick() {
        val intent = Intent(this, AddProjectActivity::class.java)
        startActivityForResult(intent, newProjectActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newProjectActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let { data ->
                val content = data.getStringExtra(PROJECT_CONTENT)
                content?.let {
                    val projectId = ProjectManager.createId()
                    val project = Project(projectId, content, Calendar.getInstance().time)
                    ProjectManager.addProject(project)
                }
            }
        } else if (requestCode == projectDetailRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let { data ->
                val id = data.getLongExtra(REMOVED_PROJECT_ID, 0)
                ProjectManager.removeProjectForId(id)
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.project_list_recycler_view)
        (recyclerView.adapter as ProjectAdapter).notifyDataSetChanged()



    }
}