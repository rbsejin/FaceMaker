package com.example.facemaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.data.Project
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.HeaderItemBinding
import com.example.facemaker.databinding.TaskItemBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class TaskAdapter(
    private val currentProject: Project,
    private val tasks: MutableList<Task>,
    private val clickListener: TaskListener /*private val onClick: (Task) -> Unit*/
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class HeaderViewHolder private constructor(private val binding: HeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Header) {
            binding.header = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderItemBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }

    class ViewHolder private constructor(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Task, clickListener: TaskListener) {
            binding.task = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        fun getCheckButton(): ToggleButton {
            return binding.checkButton
        }

        fun getImportantButton(): ToggleButton {
            return binding.importantButton
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    val itemList = mutableListOf<DataItem>()
    private var doneHeaderItem: DataItem.HeaderItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val headerItem = itemList[position] as DataItem.HeaderItem
                holder.bind(headerItem.header)

                holder.itemView.setOnClickListener {
                    if (headerItem.childList.size > 0) {
                        // open
                        itemList.addAll(position + 1, headerItem.childList)
                        headerItem.childList.clear()

                        headerItem.header.isOpen = true
                    } else {
                        // close
                        for (i in position + 1 until itemList.size) {
                            val item = itemList[i]
                            if (item is DataItem.ChildItem) {
                                headerItem.childList.add(item)
                            } else {
                                break
                            }
                        }

                        itemList.removeAll(headerItem.childList)

                        headerItem.header.isOpen = false
                    }

                    notifyDataSetChanged()
                }
            }
            is ViewHolder -> {
                val childItem: DataItem.ChildItem = itemList[position] as DataItem.ChildItem
                holder.bind(childItem.task, clickListener)

                holder.getCheckButton().setOnClickListener {
                    val task = childItem.task
                    if (task.completionDateTime == null) {
                        task.completionDateTime = Calendar.getInstance().time
                    } else {
                        task.completionDateTime = null
                    }
                    Firebase.database.reference.child("tasks").child(task.id).setValue(task)

                    /*
                    val task = childItem.task
                    task.isDone = !task.isDone

                    itemList.removeAt(position)

                    if (task.isDone) {
                        if (doneHeaderItem == null) {
                            doneHeaderItem = DataItem.HeaderItem(Header(true, "완료됨", 0))
                            itemList.add(doneHeaderItem!!)
                        }

                        if (doneHeaderItem!!.childList.size > 0) {
                            doneHeaderItem!!.childList.add(0, childItem)
                        } else {
                            val index = itemList.indexOf(doneHeaderItem) + 1
                            itemList.add(index, childItem)
                        }

                        ++doneHeaderItem!!.header.childCount
                    } else {
                        val index = itemList.indexOf(doneHeaderItem)
                        itemList.add(index, childItem)

                        --doneHeaderItem!!.header.childCount

                        if (doneHeaderItem!!.header.childCount == 0) {
                            itemList.remove(doneHeaderItem)
                            doneHeaderItem = null
                        }
                    }

                    notifyDataSetChanged()
                    */
                }

                holder.getImportantButton().setOnClickListener {
                    val task = childItem.task
                    task.isImportant = !task.isImportant
                    Firebase.database.reference.child("tasks").child(task.id).setValue(task)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is DataItem.HeaderItem -> ITEM_VIEW_TYPE_HEADER
            is DataItem.ChildItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    // DB와 데이터를 가져와서 동기화할 때 호출한다.
    fun updateTaskRecyclerView() {
        // 선조건: tasks는 생성날짜 내림차순으로 정렬되어있다.
        val doneList = mutableListOf<DataItem.ChildItem>()
        val undoneList = mutableListOf<DataItem.ChildItem>()

        for (task in tasks) {
            if (task.completionDateTime == null) {
                undoneList.add(DataItem.ChildItem(task))
            } else {
                doneList.add(DataItem.ChildItem(task))
            }
        }

        doneList.sortByDescending {
            it.task.completionDateTime
        }

        itemList.clear()
        itemList.addAll(undoneList)

        if (doneList.size > 0) {
            if (doneHeaderItem == null) {
                doneHeaderItem = DataItem.HeaderItem(Header(true, "완료됨", 0))
            } else {
                doneHeaderItem!!.childList.clear()
                doneHeaderItem!!.header.childCount = 0
            }

            doneHeaderItem!!.header.childCount = doneList.size
            itemList.add(doneHeaderItem!!)

            if (doneHeaderItem!!.header.isOpen) {
                itemList.addAll(doneList)
            } else {
                doneHeaderItem!!.childList.addAll(doneList)
                doneHeaderItem!!.header.childCount = doneList.size
            }
        }

        notifyDataSetChanged()
    }

//    @RequiresApi(Build.VERSION_CODES.N)
//    fun removeTaskForId(id: Int) {
//        val task: Task? = currentProject.getTaskForId(id)
//        if (task == null) {
//            assert(false)
//            return
//        }
//
//        currentProject.removeTaskForId(id)
//        itemList.removeIf {
//            when (it) {
//                is DataItem.ChildItem -> it.task.id == id
//                else -> false
//            }
//        }
//
//        doneHeaderItem?.apply {
//            childList.removeIf { it.task.id == id }
//        }
//
//        if (task.isDone) {
//            doneHeaderItem?.apply {
//                --header.childCount
//                if (header.childCount == 0) {
//                    itemList.remove(doneHeaderItem)
//                    doneHeaderItem = null
//                }
//            }
//        }
//
//        notifyDataSetChanged()
//    }

//    fun removeTask(position: Int) {
//        project.removeTaskAt(position)
//        notifyDataSetChanged()
//    }

    fun swapItems(from: Int, to: Int): Boolean {
        if (from !in itemList.indices) {
            return false
        }

        if (to !in itemList.indices) {
            return false
        }

        if (from < to) {
            for (i in from until to) {
                // from: 0 -> to: 4
                // 0    1
                // 1    2
                // 2    3
                // 3    4
                // 4    0

                Collections.swap(itemList, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                // from: 4 -> to: 0
                // 0    4
                // 1    0
                // 2    1
                // 3    2
                // 4    3

                Collections.swap(itemList, i, i - 1)
            }
        }

        notifyItemMoved(from, to)
        return true
    }

    fun swapTasks(from: Int, to: Int) {
        if (from < to) {
            for (i in from until to) {
                Collections.swap(tasks, i, i + 1)
            }

            for (i in from..to) {
                tasks[i].index = i
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(tasks, i, i - 1)
            }

            for (i in to..from) {
                tasks[i].index = i
            }
        }
    }

    fun isItemMoved(index: Int): Boolean {
        val headerIndex = itemList.indexOf(doneHeaderItem)

        return index < headerIndex
    }
}

sealed class DataItem {
    data class ChildItem(val task: Task) : DataItem()

    class HeaderItem(val header: Header) : DataItem() {
        /*val header = Header(true, "완료됨", 0)*/
        val childList = mutableListOf<ChildItem>()
    }
}

data class Header(var isOpen: Boolean, val name: String, var childCount: Int)

class TaskListener(val clickListener: (Task) -> Unit) {
    fun onClick(task: Task) = clickListener(task)
}

/*
class TaskAdapter(private val project: Project, private val onClick: (Task) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    class TaskViewHolder(itemView: View, val onClick: (Task) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val checkBox = itemView.findViewById<CheckBox>(R.id.task_checkBox)
        private val textView = itemView.findViewById<TextView>(R.id.task_text)
        private val importantButton = itemView.findViewById<ImageButton>(R.id.important_button)
        private var currentTask: Task? = null

        init {
            itemView.setOnClickListener {
                currentTask?.let {
                    onClick(it)
                }
            }

            checkBox.setOnClickListener {
                currentTask?.let {
                    it.isDone = checkBox.isChecked
                }
            }

            importantButton.setOnClickListener {
                currentTask?.let {
                    if (it.isImportant) {
                        it.isImportant = false
                        importantButton.setImageResource(R.drawable.baseline_star_border_black_24)
                    } else {
                        it.isImportant = true
                        importantButton.setImageResource(R.drawable.baseline_star_24)
                    }
                }
            }
        }

        fun bind(task: Task) {
            currentTask = task
            checkBox.isChecked = task.isDone
            textView.text = task.name
            if (task.isImportant) {
                importantButton.setImageResource(R.drawable.baseline_star_24)
            } else {
                importantButton.setImageResource(R.drawable.baseline_star_border_black_24)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item2, parent, false)
        return TaskViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(project.getTaskList()[position])
    }

    override fun getItemCount(): Int {
        return project.getTaskList().size
    }

    fun removeTask(position: Int) {
        project.removeTaskAt(position)
        notifyDataSetChanged()
    }

    fun swapTasks(from: Int, to: Int): Boolean {
        val taskList: List<Task> = project.getTaskList()

        if (from !in taskList.indices) {
            return false
        }

        if (to !in taskList.indices) {
            return false
        }

        if (from < to) {
            for (i in from until to) {
                Collections.swap(taskList, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(taskList, i, i - 1)
            }
        }

        notifyItemMoved(from, to)
        return true
    }
}
*/
