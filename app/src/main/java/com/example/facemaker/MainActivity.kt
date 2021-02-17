package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

const val EXTRA_MESSAGE = "com.example.facemaker.MESSAGE"
const val PROJECT_ID = "project id"

class MainActivity : AppCompatActivity() {
    private val taskListRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.project_list_recycler_view)
        recyclerView.adapter = ProjectAdapter { project -> adapterOnClick(project) }

/*        val bottomButton: View = findViewById(R.id.project_bottom)
        bottomButton.setOnClickListener {
            addButtonOnClick()
        }*/
    }

    fun sendMessage(view: View) {
        val intent = Intent(this, TaskListActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "message")
        }
        startActivityForResult(intent, taskListRequestCode)
    }

    /* Opens ProjectDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(project: Project) {
        val intent = Intent(this, TaskListActivity()::class.java)
        intent.putExtra(PROJECT_ID, project.id)
        startActivity(intent)
    }

    private fun addButtonOnClick() {
        Toast.makeText(this, "프로젝트 추가 클릭", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val recyclerView: RecyclerView = findViewById(R.id.project_list_recycler_view)
        (recyclerView.adapter as TaskAdapter).notifyDataSetChanged()
    }
}