package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.ActivityTaskListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class PlannedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskListBinding
    private lateinit var database: DatabaseReference
    private lateinit var plannedAdapter: PlannedAdapter
    private lateinit var headerAdapter: PlannedHeaderAdapter
    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_list)
        database = Firebase.database.reference

        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = getString(R.string.planned)

        plannedAdapter = PlannedAdapter(TaskListener { task -> adapterOnClick(task) })
        headerAdapter = PlannedHeaderAdapter()
        binding.taskRecyclerView.adapter = ConcatAdapter(headerAdapter, plannedAdapter)

        // DB에서 기한이 있는 작업들을 가져온다.
        val taskListListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear()

                // filter

                val filterSnapshot = snapshot.child("planned/filter")

                val day = (60 * 60 * 24 * 1000)
                val week = 7

                val filter: (Task) -> Boolean

                if (filterSnapshot.getValue<TaskFilter>() != null) {
                    headerAdapter.filter = filterSnapshot.getValue<TaskFilter>()!!
                    headerAdapter.notifyDataSetChanged()
                } else {
                    assert(false)
                    return
                }

                when (headerAdapter.filter) {
                    TaskFilter.OVERDUE -> {
                        filter = { task: Task ->
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)

                            if (task.dueDate != null) {
                                val differ = task.dueDate!!.time - calendar.time.time
                                differ < 0L
                            } else {
                                false
                            }
                        }
                    }
                    TaskFilter.TODAY -> {
                        filter = { task: Task ->
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)

                            if (task.dueDate != null) {
                                val differ = task.dueDate!!.time - calendar.time.time
                                differ in 0L until day
                            } else {
                                false
                            }
                        }
                    }
                    TaskFilter.TOMORROW -> {
                        filter = { task: Task ->
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)

                            if (task.dueDate != null) {
                                calendar.add(Calendar.DATE, 1)

                                val differ = task.dueDate!!.time - calendar.time.time
                                differ in 0L until day
                            } else {
                                false
                            }
                        }
                    }
                    TaskFilter.THIS_WEEK -> {
                        filter = { task: Task ->
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)

                            if (task.dueDate != null) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                                calendar.add(Calendar.DATE, week + 1)

                                val differ = task.dueDate!!.time - calendar.time.time
                                differ < 0L
                            } else {
                                false
                            }
                        }
                    }
                    TaskFilter.LATER -> {
                        filter = { task: Task ->
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)

                            if (task.dueDate != null) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                                calendar.add(Calendar.DATE, week + 1)

                                val differ = task.dueDate!!.time - calendar.time.time
                                differ >= 0L
                            } else {
                                false
                            }
                        }
                    }
                    TaskFilter.ALL_PLANNED -> {
                        filter = { task: Task ->
                            true
                        }
                    }
                    else -> return
                }


                // tasks

                val tasksSnapshot = snapshot.child("tasks")

                for (taskSnapshot in tasksSnapshot.children) {
                    val task: Task = taskSnapshot.getValue<Task>() ?: continue

                    if (task.dueDate != null && filter(task)) {
                        tasks.add(task)
                    }
                }

                plannedAdapter.updateItemList(tasks)
                plannedAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                if (BuildConfig.DEBUG) {
                    error("tasks를 DB에서 가져오지 못함")
                }

                finish()
            }
        }
        database.orderByChild("tasks/dueDate").addValueEventListener(taskListListener)
    }

    private fun adapterOnClick(task: Task) {
        val intent = Intent(this, TaskDetailActivity()::class.java)
        intent.putExtra(TASK_ID, task.id)
        startActivityForResult(intent, taskDetailRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == taskDetailRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let { data ->
                val id = data.getIntExtra(REMOVED_PROJECT_ID, 0)
                database.child("tasks/$id").removeValue()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.planed_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.project_delete_item -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}