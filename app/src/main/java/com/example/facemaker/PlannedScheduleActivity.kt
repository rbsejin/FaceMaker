//package com.example.facemaker
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.RecyclerView
//import com.example.facemaker.data.Task
//
//class PlannedScheduleActivity : AppCompatActivity() {
//    private val newTaskActivityRequestCode = 1
//    private val taskDetailRequestCode = 2
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_planned_schedule)
//
//        val recyclerView: RecyclerView = findViewById(R.id.task_recycler_view)
//        recyclerView.adapter = PlanedScheduleAdapter(TaskListener { task -> adapterOnClick(task) })
//    }
//
//    private fun adapterOnClick(task: Task) {
//        val intent = Intent(this, TaskDetailActivity()::class.java)
//        intent.putExtra(TASK_ID, task.id)
//        //startActivity(intent)
//        startActivityForResult(intent, taskDetailRequestCode)
//    }
//}