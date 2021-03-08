package com.example.facemaker

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.data.Project
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.ActivityTaskListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

const val TASK_ID = "task id"
const val REMOVED_PROJECT_ID = "removedTaskId"

class TaskListActivity() : AppCompatActivity(),
    ProjectDialogFragment.ProjectCreationDialogListener {
    private val newTaskActivityRequestCode = 1
    private val taskDetailRequestCode = 2

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var currentProject: Project
    private val tasks = mutableListOf<Task>()

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
            // 프로젝트 생성했을 때
            val key: String? = database.child("projects").push().key
            if (key == null) {
                if (BuildConfig.DEBUG) {
                    error("key must not be null")
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


            // 프로젝트 생성 다이얼로그
            ProjectDialogFragment("").also { dialog ->
                dialog.isCancelable = false
                dialog.show(supportFragmentManager, ProjectDialogFragment.NEW_PROJECT_TAG)
            }

            currentProjectId = key
        } else {
            // 프로젝트를 열었을 때
            currentProjectId = bundle?.getString(PROJECT_ID) ?: return
        }

        // DB에서 현재 프로젝트 데이터를 가져온다.
        database.child("projects/$currentProjectId").get().addOnSuccessListener {
            currentProject = it.getValue<Project>() ?: return@addOnSuccessListener
            binding.toolbarLayout.title = currentProject.name

            // 프로젝트 이름을 클릭했을 때 프로젝트를 변경하는 다이얼로그가 열린다.
            binding.toolbarLayout.setOnClickListener {
                ProjectDialogFragment(currentProject.name).also { dialog ->
                    dialog.isCancelable = false
                    dialog.show(
                        supportFragmentManager,
                        ProjectDialogFragment.UPDATE_PROJECT_NAME_TAG
                    )
                }
            }

            binding.toolbarLayout.title = currentProject.name
            taskAdapter =
                TaskAdapter(currentProject, tasks, TaskListener { task -> adapterOnClick(task) })
            binding.taskRecyclerView.adapter = taskAdapter

            // DB에서 현재 프로젝트의 작업들을 가져온다.
            val projectListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tasks.clear()

                    for (taskSnapshot in snapshot.children) {
                        val task: Task = taskSnapshot.getValue<Task>() ?: continue
                        if (task.projectId == currentProjectId) {
                            tasks.add(task)
                        }
                    }

                    tasks.sortBy { task -> task.index }
                    taskAdapter.updateTaskRecyclerView()
                }

                override fun onCancelled(error: DatabaseError) {
                    if (BuildConfig.DEBUG) {
                        error("tasks를 DB에서 가져오지 못함")
                    }

                    finish()
                }

            }
            database.child("tasks").orderByChild("index").addValueEventListener(projectListener)
        }.addOnFailureListener {
            if (BuildConfig.DEBUG) {
                error("project를 DB에서 가져오지 못함")
            }

            finish()
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        // 키보드가 열려있을 때 뒤로가기 버튼을 눌렀을 경우 입력창이 사라지도록 처리
        binding.addTaskText.setOnBackPressListener {
            binding.addTaskLayout.visibility = View.GONE
            binding.addTaskText.setText("")

            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.addTaskText.windowToken, 0)

            binding.fab.visibility = View.VISIBLE
        }

        // 입력창에서 엔터를 입력했을 때 작업 추가가 되도록 처리
        binding.addTaskText.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    addTask(binding.addTaskText.text.toString())
                    binding.addTaskText.setText("")
                    true
                }
                else -> false
            }

            true
        }

        binding.addTaskButton.setOnClickListener {
            addTask(binding.addTaskText.text.toString())
            binding.addTaskText.setText("")
        }

        // delete to swipe
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            private var dragFrom: Int = -1
            private var dragTo: Int = -1

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                if (!taskAdapter.isItemMoved(fromPosition)) {
                    return false
                }

                if (!taskAdapter.isItemMoved(toPosition)) {
                    return false
                }

                //taskAdapter.swapItems(fromPosition, toPosition)
                taskAdapter.notifyItemMoved(fromPosition, toPosition)

                if (dragFrom == -1) {
                    dragFrom = fromPosition
                }

                dragTo = toPosition

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val task: Task =
                            (taskAdapter.itemList[viewHolder.adapterPosition] as DataItem.ChildItem).task
                        database.child("tasks").child(task.id).removeValue()
                        taskAdapter.notifyDataSetChanged()
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    // tasks: fromPosition ~ toPosition update
                    val fromItem = taskAdapter.itemList[dragFrom] as DataItem.ChildItem
                    val toItem = taskAdapter.itemList[dragTo] as DataItem.ChildItem

                    val from = fromItem.task.index
                    val to = toItem.task.index

                    if (from < to) {
                        for (i in from until to) {
                            Collections.swap(tasks, i, i + 1)
                        }
                    } else {
                        for (i in from downTo to + 1) {
                            Collections.swap(tasks, i, i - 1)
                        }
                    }

                    val begin = kotlin.math.min(from, to)
                    val end = kotlin.math.max(from, to)

                    for (i in begin..end) {
                        tasks[i].index = i
                    }

                    val childUpdates =
                        tasks.subList(begin, end + 1).map { "tasks/${it.id}/index" to it.index }
                            .toMap()
                    database.updateChildren(childUpdates)
                }

                dragFrom = -1
                dragTo = -1
            }
        }).apply {
            attachToRecyclerView(binding.taskRecyclerView)
        }

        // 플로팅 버튼을 클릭했을 때 입력창과 키보드를 띄운다.
        binding.fab.setOnClickListener {
            binding.addTaskLayout.visibility = View.VISIBLE
            binding.fab.visibility = View.GONE
            binding.addTaskText.requestFocus()

            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.addTaskText, 0)
        }
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

    override fun onDialogPositiveClick(projectName: String) {
        binding.toolbarLayout.title = projectName
        database.child("projects/${currentProject.id}/name").setValue(projectName)
    }

    override fun onDialogNegativeClick(isDeleted: Boolean) {
        if (isDeleted) {
            removeCurrentProject()
            finish()
        }
    }

    private fun removeCurrentProject() {
        database.child("projects/${currentProject.id}").removeValue()

        val childUpdates = tasks.map { "tasks/${it.id}" to null }.toMap()
        database.updateChildren(childUpdates)
    }

    private fun addTask(taskName: String): Boolean {
        if (taskName.isEmpty()) {
            return false
        }

        val key: String = database.child("tasks").push().key ?: return false
        val task = Task(
            key,
            currentProject.id,
            auth.currentUser.uid,
            taskName,
            Calendar.getInstance().time
        )

        tasks.add(0, task)

        var i = 0
        for (task in tasks) {
            task.index = i++
        }

        val taskMap = tasks.map { "tasks/${it.id}" to it }.toMap()
        database.updateChildren(taskMap)
        return true
    }

    companion object {
        const val DEFAULT_PROJECT_NAME = "제목 없는 목록"
    }
}
