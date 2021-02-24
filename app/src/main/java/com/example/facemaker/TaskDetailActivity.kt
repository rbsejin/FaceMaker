package com.example.facemaker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

const val REMOVED_TASK_ID = "removedTaskId"

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        /* Connect variables to UI elements. */
        val taskContent: EditText = findViewById(R.id.task_detail_content)
        val taskCheckBox: CheckBox = findViewById(R.id.task_detail_checkBox)

        val bundle: Bundle? = intent.extras
        val currentTaskId: Int = bundle?.getInt(TASK_ID) ?: return
        val currentTask = ProjectManager.getTaskForId(currentTaskId) ?: return

        taskContent.setText(currentTask.content, TextView.BufferType.EDITABLE)
        taskCheckBox.isChecked = currentTask.isDone

        // 체크박스 클릭했을 때 이벤트 추가
        val checkBox: CheckBox = findViewById(R.id.task_detail_checkBox)
        checkBox.setOnClickListener {
            currentTask.isDone = checkBox.isChecked
            ProjectManager.save(filesDir)
        }

        // EditText focus 가 변경될 때 이벤트 추가
        /*val editText: EditText = findViewById(R.id.task_detail_content)
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                currentTask.content = editText.text.toString()
            }
        }*/

        // 변경될 때마다가 아니라 초점을 잃었을 때만 변경해야하지만
        // 임시로 EditText 가 변경될 때 이벤트 추가
        val editText: EditText = findViewById(R.id.task_detail_content)
        editText.addTextChangedListener {
            currentTask.content = editText.text.toString()
        }

        // Task 삭제하기
        val deleteButton: ImageButton = findViewById(R.id.task_detail_delete_button)
        deleteButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("삭제하시겠습니까?")
                .setTitle("계속할까요?")
                .setNegativeButton("취소", null)
                .setPositiveButton("삭제") { _, _ ->
                    val resultIntent = Intent()
                    resultIntent.putExtra(REMOVED_TASK_ID, currentTaskId)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        val taskDateAdapter = TaskDateAdapter(currentTask)
        val taskFileAdapter = TaskFileAdapter(currentTask)

        val recyclerView = findViewById<RecyclerView>(R.id.task_detail_recycler_view)
        recyclerView.adapter = ConcatAdapter(taskDateAdapter, taskFileAdapter)
    }

    override fun onStop() {
        ProjectManager.save(filesDir)
        super.onStop()
    }
}
