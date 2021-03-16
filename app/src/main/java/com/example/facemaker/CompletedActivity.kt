package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.ActivityTaskListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class CompletedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskListBinding
    private lateinit var database: DatabaseReference
    private lateinit var groupByAdapter: GroupByAdapter
    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_list)
        database = Firebase.database.reference

        // 나의 하루에서 작업 추가 미구현으로 버튼 숨김
        binding.fab.visibility = View.INVISIBLE

        // 배경색 지정
        binding.root.setBackgroundColor(resources.getColor(R.color.all_task_list_title_color))
        binding.toolbarLayout.setContentScrimColor(resources.getColor(R.color.all_task_list_title_color))
        binding.toolbarLayout.setBackgroundColor(resources.getColor(R.color.all_task_list_title_color))
//        binding.toolbarLayout.setCollapsedTitleTextColor(resources.getColor(R.color.important_task_list_title_color))
//        binding.toolbarLayout.setExpandedTitleColor(resources.getColor(R.color.important_task_list_title_color))

        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = getString(R.string.completion)

        groupByAdapter = GroupByAdapter(TaskListener { task -> adapterOnClick(task) })
        binding.taskRecyclerView.adapter = groupByAdapter

        // DB에서 기한이 있는 작업들을 가져온다.
        val taskListListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear()

                // filter
                val filterSnapshot =
                    snapshot.child("users/${Firebase.auth.currentUser.uid}/planned/filter")

                val day = (60 * 60 * 24 * 1000)
                val week = 7

                val filter: (Task) -> Boolean

                // completion filter
                val completionFilterSnapshot =
                    snapshot.child("users/${Firebase.auth.currentUser.uid}/planned/completionFilter")

                groupByAdapter.completionFilter =
                    completionFilterSnapshot.getValue<Boolean>() ?: false

                // tasks
                val tasksSnapshot = snapshot.child("tasks")

                for (taskSnapshot in tasksSnapshot.children) {
                    val task: Task = taskSnapshot.getValue<Task>() ?: continue

                    if (task.userId != Firebase.auth.currentUser.uid) {
                        continue
                    }

                    if (task.completionDateTime == null) {
                        continue
                    }

                    tasks.add(task)
                }

                groupByAdapter.updateItemList(tasks)
                groupByAdapter.notifyDataSetChanged()
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
                val id = data.getIntExtra(REMOVED_TASK_ID, 0)
                database.child("tasks/$id").removeValue()
            }
        }
    }
}
