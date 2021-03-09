package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

        plannedAdapter =  PlannedAdapter(TaskListener { task -> adapterOnClick(task) })
        headerAdapter = PlannedHeaderAdapter()
        binding.taskRecyclerView.adapter = ConcatAdapter(headerAdapter, plannedAdapter)

        // DB에서 기한이 있는 작업들을 가져온다.
        val taskListListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear()

                for (taskSnapshot in snapshot.children) {
                    val task: Task = taskSnapshot.getValue<Task>() ?: continue
                    if (task.dueDate != null) {
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
        database.child("tasks").orderByChild("dueDate").addValueEventListener(taskListListener)
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
}