package com.example.facemaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TodayTaskListActivity() : AppCompatActivity() {
    private val newTaskActivityRequestCode = 1
    private val taskDetailRequestCode = 2

    private lateinit var todayTaskAdapter: TodayTaskAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today_task_list)

        val dateText: TextView = findViewById(R.id.date_text)
        val dateFormat = SimpleDateFormat("MM월 dd일 EE요일")
        dateText.text = dateFormat.format(Calendar.getInstance().time)

        recyclerView = findViewById(R.id.task_recycler_view)

        todayTaskAdapter =
            TodayTaskAdapter(ProjectManager.getTodayTaskList()) { task -> adapterOnClick(task) }
        recyclerView.adapter = todayTaskAdapter

        // 아이템간 구분선
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // delete to swipe
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return todayTaskAdapter.swapTasks(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                todayTaskAdapter.removeTodayTask(viewHolder.adapterPosition)
            }
        }).apply {
            attachToRecyclerView((recyclerView))
        }
    }

    /* Opens TaskDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(task: Task) {
        val intent = Intent(this, TaskDetailActivity()::class.java)
        intent.putExtra(TASK_ID, task.id)
        //startActivity(intent)
        startActivityForResult(intent, taskDetailRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newTaskActivityRequestCode && resultCode == Activity.RESULT_OK) {
/*            data?.let { data ->
                val name = data.getStringExtra(TASK_NAME)
                name?.let {
                    val taskId = currentProject.createId()
                    val task = Task(taskId, currentProject.id, name, Calendar.getInstance().time)
                    currentProject.addTask(task)
                }
            }*/
        } else if (requestCode == taskDetailRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let { data ->
                val id = data.getIntExtra(REMOVED_PROJECT_ID, 0)
                ProjectManager.removeTaskForId(id)
                todayTaskAdapter.notifyDataSetChanged()
            }
        }

        ProjectManager.save(filesDir)
    }

/*    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
                        val resultIntent = Intent()
                        resultIntent.putExtra(REMOVED_PROJECT_ID, currentProject.id)
                        setResult(Activity.RESULT_OK, resultIntent)
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
        ProjectManager.save(filesDir)
        super.onStop()
    }*/

    override fun onStart() {
        super.onStart()

        todayTaskAdapter.todayTaskList = ProjectManager.getTodayTaskList()
        todayTaskAdapter.notifyDataSetChanged()
    }
}