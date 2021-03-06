package com.example.facemaker

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.facemaker.data.Project
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.ActivityTaskListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

const val TASK_ID = "task id"
const val REMOVED_PROJECT_ID = "removedTaskId"

class TaskListActivity() : AppCompatActivity(),
    ProjectCreationDialogFragment.ProjectCreationDialogListener {
    private val newTaskActivityRequestCode = 1
    private val taskDetailRequestCode = 2

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var currentProject: Project
//    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_list)
        database = Firebase.database.reference
        auth = Firebase.auth

        val bundle: Bundle? = intent.extras
        val currentProjectId: String

        val isAddingProject = bundle?.getBoolean(ADD_PROJECT) ?: false
        if (isAddingProject) {
            // 1. 프로젝트 생성
            val key: String? = database.child("projects").push().key
            if (key == null) {
                if (BuildConfig.DEBUG) {
                    error("key must be not null")
                }
                return
            }

            val project = Project(
                key,
                auth.currentUser.uid,
                getString(R.string.none_name_project),
                Calendar.getInstance().time
            )
            database.child("projects").child(key).setValue(project)

            // 3. 프로젝트 생성 다이얼로그
            ProjectCreationDialogFragment("").also {
                it.show(supportFragmentManager, ProjectCreationDialogFragment.NEW_PROJECT_TAG)
            }

            currentProjectId = key
        } else {
            currentProjectId = bundle?.getString(PROJECT_ID) ?: return
        }

        database.child("projects/$currentProjectId").get().addOnSuccessListener {
            currentProject = it.getValue<Project>() ?: return@addOnSuccessListener

            binding.taskListProjectName.text = currentProject.name

            binding.taskListProjectName.setOnClickListener {
                ProjectCreationDialogFragment(currentProject.name).also {
                    it.show(supportFragmentManager, ProjectCreationDialogFragment.UPDATE_PROJECT_NAME_TAG)
                }
            }
        }.addOnFailureListener {
            if (BuildConfig.DEBUG) {
                error("project 를 DB에서 가져오지 못함")
            }
        }


        //taskAdapter= TaskAdapter(currentProject) { task -> adapterOnClick(task) }

//        taskAdapter = TaskAdapter(currentProject, TaskListener { task -> adapterOnClick(task) })

//        recyclerView.adapter = taskAdapter

        // 아이템간 구분선
/*        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)*/

        // delete to swipe
/*        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return taskAdapter.swapTasks(viewHolder.adapterPosition, target.adapterPosition)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                taskAdapter.removeTask(viewHolder.adapterPosition)
            }
        }).apply {
            attachToRecyclerView((recyclerView))
        }*/

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            fabOnClick()
        }
    }

    private fun fabOnClick() {
//        val intent = Intent(this, AddTaskActivity::class.java)
//        startActivityForResult(intent, newTaskActivityRequestCode)
    }

    /* Opens TaskDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(task: Task) {
//        val intent = Intent(this, TaskDetailActivity()::class.java)
//        intent.putExtra(PROJECT_ID, currentProject.id)
//        intent.putExtra(TASK_ID, task.id)
//        //startActivity(intent)
//        startActivityForResult(intent, taskDetailRequestCode)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        if (requestCode == newTaskActivityRequestCode && resultCode == Activity.RESULT_OK) {
//            data?.let { data ->
//                val name = data.getStringExtra(TASK_NAME)
//                name?.let {
//                    val taskId = currentProject.createId()
//                    val task = Task(taskId, currentProject.id, name, Calendar.getInstance().time)
//                    taskAdapter.addTask(task)
//                    taskAdapter.notifyDataSetChanged()
//                }
//            }
//        } else if (requestCode == taskDetailRequestCode && resultCode == Activity.RESULT_OK) {
//            data?.let { data ->
//                val id = data.getIntExtra(REMOVED_PROJECT_ID, 0)
//                taskAdapter.removeTaskForId(id)
//            }
//        }
//
//        val recyclerView: RecyclerView = findViewById(R.id.task_recycler_view)
//        (recyclerView.adapter as TaskAdapter).notifyDataSetChanged()
//        ProjectManager.save(filesDir)
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
                        removeCurrentProject()
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
        super.onStop()
    }


    override fun onDialogPositiveClick(projectName: String) {
        binding.taskListProjectName.text = projectName
        database.child("projects/${currentProject.id}/name")
            .setValue(projectName)
    }

    override fun onDialogNegativeClick(isDeleted: Boolean) {
        if (isDeleted) {
            removeCurrentProject()
            finish()
        }
    }

    private fun removeCurrentProject() {
        database.child("projects/${currentProject.id}").removeValue()
        // db에 프로젝트에 포함된 task도 삭제 해야함
    }

    companion object {
        const val DEFAULT_PROJECT_NAME = "제목 없는 목록"
    }
}