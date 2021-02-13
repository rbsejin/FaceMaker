package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

const val REMOVED_TASK_ID = "removedTaskId"

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        var currentTaskId: Long? = null

        /* Connect variables to UI elements. */
        val taskContent: TextView = findViewById(R.id.task_detail_content)
        val taskCheckBox: CheckBox = findViewById(R.id.task_detail_checkBox)

        val bundle: Bundle? = intent.extras
        currentTaskId = bundle?.getLong(TASK_ID)

        currentTaskId?.let {
            val currentTask = TaskManager.getTaskForId(it)
            currentTask?.let {
                taskContent.text = currentTask.content
                taskCheckBox.isChecked = currentTask.isDone

                // 체크박스 클릭했을 때 이벤트 추가
                val checkBox: CheckBox = findViewById(R.id.task_detail_checkBox)
                checkBox.setOnClickListener {
                    currentTask.isDone = checkBox.isChecked
                }

                // EditText focus 가 변경될 때 이벤트 추가
                /*val editText: EditText = findViewById(R.id.task_detail_content)
                editText.setOnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        currentTask.content = editText.text.toString()
                    }
                }*/

                // 변경될 때마다가 아니라 초점을 잃었을 때만 변경해야하지만
                // 임시로 EditText가 변경될 때 이벤트 추가
                val editText: EditText = findViewById(R.id.task_detail_content)
                editText.addTextChangedListener {
                    currentTask.content = editText.text.toString()
                }

                // Task 삭제하기
                val deleteButton: ImageButton = findViewById(R.id.task_detail_delete_button)
                deleteButton.setOnClickListener {
                    val resultIntent = Intent()
                    resultIntent.putExtra(REMOVED_TASK_ID, currentTaskId)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }
}
