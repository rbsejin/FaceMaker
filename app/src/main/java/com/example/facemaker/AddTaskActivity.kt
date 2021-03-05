//package com.example.facemaker
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.textfield.TextInputEditText
//
//const val TASK_NAME = "name"
//
//class AddTaskActivity : AppCompatActivity() {
//    private lateinit var addTaskName: TextInputEditText
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_task)
//
//        findViewById<Button>(R.id.add_task_button).setOnClickListener {
//            addTask()
//        }
//        addTaskName = findViewById(R.id.add_task_name)
//    }
//
//    private fun addTask() {
//        val resultIntent = Intent()
//
//        if (addTaskName.text.isNullOrEmpty()) {
//            setResult(Activity.RESULT_CANCELED, resultIntent)
//        } else {
//            val name = addTaskName.text.toString()
//            resultIntent.putExtra(TASK_NAME, name)
//            setResult(Activity.RESULT_OK, resultIntent)
//        }
//        finish()
//    }
//}