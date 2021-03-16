package com.example.facemaker

import android.app.Activity
<<<<<<< HEAD
import android.content.Intent
import android.os.Build
import android.os.Bundle
=======
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
>>>>>>> 13f26af9cd0d3cd2bd13ff7d4060cef62d62378e
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
<<<<<<< HEAD
=======
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
>>>>>>> 13f26af9cd0d3cd2bd13ff7d4060cef62d62378e
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
import java.text.SimpleDateFormat
import java.util.*

class MyDayActivity : AppCompatActivity() {
    private val title = "나의 하루"

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var doneTaskAdapter: DoneTaskAdapter
    private lateinit var projectName: String

    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_list)
        database = Firebase.database.reference
        auth = Firebase.auth

        // 나의 하루에서 작업 추가 미구현으로 버튼 숨김
        binding.fab.visibility = View.INVISIBLE

        // 배경색 지정
        binding.root.setBackgroundColor(resources.getColor(R.color.my_day_background_color))
        binding.toolbarLayout.setContentScrimColor(resources.getColor(R.color.my_day_background_color))
        binding.toolbarLayout.setBackgroundColor(resources.getColor(R.color.my_day_background_color))


        // DB에서 현재 프로젝트 데이터를 가져온다.
        binding.toolbarLayout.title = title
        setSupportActionBar(binding.toolbar)

        taskAdapter = TaskAdapter(TaskListener { task -> adapterOnClick(task) })
        doneTaskAdapter = DoneTaskAdapter(TaskListener { task -> adapterOnClick(task) })
        binding.taskRecyclerView.adapter = ConcatAdapter(taskAdapter, doneTaskAdapter)

        // DB에서 현재 프로젝트의 작업들을 가져온다.
        val projectListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear()

                val today = Calendar.getInstance().time

                for (taskSnapshot in snapshot.children) {
                    val task: Task = taskSnapshot.getValue<Task>() ?: continue

                    if (task.myDay == null) {
                        continue
                    }

                    val dateFormat = SimpleDateFormat("Y년 M월 d일 (EE)")

                    if (dateFormat.format(task.myDay) == dateFormat.format(today)) {
                        tasks.add(task)
                    }
                }

                tasks.sortBy { task -> task.myDay }

                val undoneTaskList = mutableListOf<Task>()
                val doneTaskList = mutableListOf<Task>()

                for (task in tasks) {
                    if (task.completionDateTime == null) {
                        undoneTaskList.add(task)
                    } else {
                        doneTaskList.add(task)
                    }
                }

                taskAdapter.itemList = undoneTaskList
                taskAdapter.notifyDataSetChanged()

                doneTaskAdapter.updateItemList(doneTaskList)
                doneTaskAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                if (BuildConfig.DEBUG) {
                    error("tasks를 DB에서 가져오지 못함")
                }

                finish()
            }
        }
        database.child("tasks").orderByChild("myDay").addValueEventListener(projectListener)

        /* 이벤트 처리 */

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
//        binding.addTaskText.setOnEditorActionListener { v, actionId, event ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_DONE -> {
//                    addTask(binding.addTaskText.text.toString())
//                    binding.addTaskText.setText("")
//                    true
//                }
//                else -> false
//            }
//
//            true
//        }

//        binding.addTaskButton.setOnClickListener {
//            addTask(binding.addTaskText.text.toString())
//            binding.addTaskText.setText("")
//        }

        // 플로팅 버튼을 클릭했을 때 입력창과 키보드를 띄운다.
        binding.fab.setOnClickListener {
            binding.addTaskLayout.visibility = View.VISIBLE
            binding.fab.visibility = View.GONE
            binding.addTaskText.requestFocus()

            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.addTaskText, 0)
        }

        // 프로젝트 이름을 클릭했을 때 프로젝트를 변경하는 다이얼로그가 열린다.
        binding.toolbarLayout.setOnClickListener {
            ProjectDialogFragment(projectName).also { dialog ->
                dialog.isCancelable = false
                dialog.show(
                    supportFragmentManager,
                    ProjectDialogFragment.UPDATE_PROJECT_NAME_TAG
                )
            }
        }

        // delete to swipe
<<<<<<< HEAD
//        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
//            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
//            ItemTouchHelper.LEFT
//        ) {
//            private var dragFrom: Int = -1
//            private var dragTo: Int = -1
//
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                val fromPosition = viewHolder.adapterPosition
//                val toPosition = target.adapterPosition
//
//                if (!taskAdapter.isItemMoved(fromPosition, toPosition)) {
//                    return false
//                }
//
//                taskAdapter.notifyItemMoved(fromPosition, toPosition)
//
//                if (dragFrom == -1) {
//                    dragFrom = fromPosition
//                }
//
//                dragTo = toPosition
//
//                return true
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                when (direction) {
//                    ItemTouchHelper.LEFT -> {
//                        when (viewHolder) {
//                            is TaskAdapter.ViewHolder -> {
//                                val task: Task = taskAdapter.itemList[viewHolder.adapterPosition]
//                                database.child("tasks").child(task.id).removeValue()
//                                taskAdapter.notifyDataSetChanged()
//                            }
//                            is DoneTaskAdapter.ViewHolder -> {
//                                val task: Task =
//                                    (doneTaskAdapter.itemList[viewHolder.adapterPosition] as DataItem.ChildItem).task
//                                database.child("tasks").child(task.id).removeValue()
//                                doneTaskAdapter.notifyDataSetChanged()
//                            }
//                            is DoneTaskAdapter.HeaderViewHolder -> {
//                                // 아무것도 안함
//                            }
//                            else -> {
//                            }
//                        }
//                    }
//                }
//            }
//
//            override fun getSwipeDirs(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder
//            ): Int {
//                if (viewHolder is DoneTaskAdapter.HeaderViewHolder) {
//                    return 0
//                }
//
//                return super.getSwipeDirs(recyclerView, viewHolder)
//            }
//
//            override fun getDragDirs(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder
//            ): Int {
//                if (viewHolder is DoneTaskAdapter.HeaderViewHolder) {
//                    return 0
//                }
//
//                if (viewHolder is DoneTaskAdapter.ViewHolder) {
//                    return ItemTouchHelper.LEFT
//                }
//
//                return super.getDragDirs(recyclerView, viewHolder)
//            }
//
//            @RequiresApi(Build.VERSION_CODES.N)
//            override fun clearView(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder
//            ) {
//                super.clearView(recyclerView, viewHolder)
//
//                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
//                    // tasks: fromPosition ~ toPosition update
//                    val fromItem = taskAdapter.itemList[dragFrom]
//                    val toItem = taskAdapter.itemList[dragTo]
//
//                    val from = fromItem.index
//                    val to = toItem.index
//
//                    if (from < to) {
//                        for (i in from until to) {
//                            Collections.swap(tasks, i, i + 1)
//                        }
//                    } else {
//                        for (i in from downTo to + 1) {
//                            Collections.swap(tasks, i, i - 1)
//                        }
//                    }
//
//                    val begin = kotlin.math.min(from, to)
//                    val end = kotlin.math.max(from, to)
//
//                    for (i in begin..end) {
//                        tasks[i].index = i
//                    }
//
//                    val childUpdates =
//                        tasks.subList(begin, end + 1).map { "tasks/${it.id}/index" to it.index }
//                            .toMap()
//                    database.updateChildren(childUpdates)
//                }
//
//                dragFrom = -1
//                dragTo = -1
//            }
//        }).apply {
//            attachToRecyclerView(binding.taskRecyclerView)
//        }
=======
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

                if (!taskAdapter.isItemMoved(fromPosition, toPosition)) {
                    return false
                }

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
                        when (viewHolder) {
                            is TaskAdapter.ViewHolder -> {
                                val task: Task = taskAdapter.itemList[viewHolder.adapterPosition]
                                database.child("tasks").child(task.id).removeValue()
                                taskAdapter.notifyDataSetChanged()
                            }
                            is DoneTaskAdapter.ViewHolder -> {
                                val task: Task =
                                    (doneTaskAdapter.itemList[viewHolder.adapterPosition] as DataItem.ChildItem).task
                                database.child("tasks").child(task.id).removeValue()
                                doneTaskAdapter.notifyDataSetChanged()
                            }
                            is DoneTaskAdapter.HeaderViewHolder -> {
                                // 아무것도 안함
                            }
                            else -> {
                            }
                        }
                    }
                }
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder is DoneTaskAdapter.HeaderViewHolder) {
                    return 0
                }

                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun getDragDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder is DoneTaskAdapter.HeaderViewHolder) {
                    return 0
                }

                if (viewHolder is DoneTaskAdapter.ViewHolder) {
                    return ItemTouchHelper.LEFT
                }

                return super.getDragDirs(recyclerView, viewHolder)
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    // tasks: fromPosition ~ toPosition update
                    val fromItem = taskAdapter.itemList[dragFrom]
                    val toItem = taskAdapter.itemList[dragTo]

                    val from = fromItem.index
                    val to = toItem.index

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
>>>>>>> 13f26af9cd0d3cd2bd13ff7d4060cef62d62378e
    }

    /* Opens TaskDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(task: Task) {
        val intent = Intent(this, TaskDetailActivity()::class.java)
        intent.putExtra(TASK_ID, task.id)
        //startActivity(intent)
        startActivityForResult(intent, taskDetailRequestCode)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == taskDetailRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let { data ->
                val id = data.getIntExtra(REMOVED_TASK_ID, 0)
                database.child("tasks/$id").removeValue()
            }
        }
    }

<<<<<<< HEAD
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.project_option_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.project_delete_item -> {
//                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//                builder.setMessage("삭제하시겠습니까?")
//                    .setTitle("계속할까요?")
//                    .setNegativeButton("취소", null)
//                    .setPositiveButton("삭제") { _, _ ->
//
//                        finish()
//                    }
//                val dialog: AlertDialog = builder.create()
//                dialog.show()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
=======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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

                        finish()
                    }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
>>>>>>> 13f26af9cd0d3cd2bd13ff7d4060cef62d62378e

//    private fun addTask(taskName: String): Boolean {
//        if (taskName.isEmpty()) {
//            return false
//        }
//
//        val key: String = database.child("tasks").push().key ?: return false
//        val task = Task(
//            key,
//            projectId,
//            auth.currentUser.uid,
//            taskName,
//            Calendar.getInstance().time
//        )
//
//        tasks.add(0, task)
//
//        var i = 0
//        for (task in tasks) {
//            task.index = i++
//        }
//
//        val taskMap = tasks.map { "tasks/${it.id}" to it }.toMap()
//        database.updateChildren(taskMap)
//        return true
//    }
}
