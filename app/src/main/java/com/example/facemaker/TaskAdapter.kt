//package com.example.facemaker
//
//import android.os.Build
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.ToggleButton
//import androidx.annotation.RequiresApi
//import androidx.recyclerview.widget.RecyclerView
//import com.example.facemaker.data.Project
//import com.example.facemaker.data.Task
//import com.example.facemaker.databinding.HeaderItemBinding
//import com.example.facemaker.databinding.TaskItemBinding
//
//private const val ITEM_VIEW_TYPE_HEADER = 0
//private const val ITEM_VIEW_TYPE_ITEM = 1
//
//class TaskAdapter(
//    private val currentProject: Project,
//    private val clickListener: TaskListener /*private val onClick: (Task) -> Unit*/
//) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    class HeaderViewHolder private constructor(private val binding: HeaderItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: Header) {
//            binding.header = item
//            binding.executePendingBindings()
//        }
//
//        companion object {
//            fun from(parent: ViewGroup): HeaderViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding = HeaderItemBinding.inflate(layoutInflater, parent, false)
//                return HeaderViewHolder(binding)
//            }
//        }
//    }
//
//    class ViewHolder private constructor(private val binding: TaskItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        private var currentTask: Task? = null
//
//        init {
//            binding.importantButton.setOnClickListener {
//                currentTask?.let {
//                    it.isImportant = !it.isImportant
//                    binding.task = currentTask
//                }
//            }
//        }
//
//        fun bind(item: Task, clickListener: TaskListener) {
//            currentTask = item
//            binding.task = item
//            binding.clickListener = clickListener
//            binding.executePendingBindings()
//        }
//
//        fun getCheckButton(): ToggleButton {
//            return binding.checkButton
//        }
//
//        companion object {
//            fun from(parent: ViewGroup): ViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
//                return ViewHolder(binding)
//            }
//        }
//    }
//
///*    private val undoneHeaderItem: DataItem.HeaderItem =
//        itemList.find { it is DataItem.HeaderItem } as DataItem.HeaderItem*/
//
//    private val itemList = mutableListOf<DataItem>()
//    private var doneHeaderItem: DataItem.HeaderItem? = null
//
//    init {
//        val doneList = mutableListOf<DataItem>()
//        val undoneList = mutableListOf<DataItem>()
//
//        for (task in currentProject.getTaskList()) {
//            if (task.isDone) {
//                doneList.add(DataItem.ChildItem(task))
//            } else {
//                undoneList.add(DataItem.ChildItem(task))
//            }
//        }
//
//        itemList.addAll(undoneList)
//        if (doneList.size > 0) {
//            doneHeaderItem = DataItem.HeaderItem(Header(true, "완료됨", 0))
//            doneHeaderItem!!.header.childCount = doneList.size
//            itemList.add(doneHeaderItem!!)
//            itemList.addAll(doneList)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
//            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
//            else -> throw ClassCastException("Unknown viewType $viewType")
//        }
//
//        return ViewHolder.from(parent)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (holder) {
//            is HeaderViewHolder -> {
//                val headerItem = itemList[position] as DataItem.HeaderItem
//                holder.bind(headerItem.header)
//
//                holder.itemView.setOnClickListener {
//                    if (headerItem.childList.size > 0) {
//                        // open
//                        itemList.addAll(position + 1, headerItem.childList)
//                        headerItem.childList.clear()
//
//                        headerItem.header.isOpen = true
//                    } else {
//                        // close
//                        for (i in position + 1 until itemList.size) {
//                            val item = itemList[i]
//                            if (item is DataItem.ChildItem) {
//                                headerItem.childList.add(item)
//                            } else {
//                                break
//                            }
//                        }
//
//                        itemList.removeAll(headerItem.childList)
//
//                        headerItem.header.isOpen = false
//                    }
//
//                    notifyDataSetChanged()
//                }
//            }
//            is ViewHolder -> {
//                val childItem: DataItem.ChildItem = itemList[position] as DataItem.ChildItem
//                holder.bind(childItem.task, clickListener)
//
//                holder.getCheckButton().setOnClickListener {
//                    val task = childItem.task
//                    task.isDone = !task.isDone
//
//                    itemList.removeAt(position)
//
//                    if (task.isDone) {
//                        if (doneHeaderItem == null) {
//                            doneHeaderItem = DataItem.HeaderItem(Header(true, "완료됨", 0))
//                            itemList.add(doneHeaderItem!!)
//                        }
//
//                        if (doneHeaderItem!!.childList.size > 0) {
//                            doneHeaderItem!!.childList.add(0, childItem)
//                        } else {
//                            val index = itemList.indexOf(doneHeaderItem) + 1
//                            itemList.add(index, childItem)
//                        }
//
//                        ++doneHeaderItem!!.header.childCount
//                    } else {
//                        val index = itemList.indexOf(doneHeaderItem)
//                        itemList.add(index, childItem)
//
//                        --doneHeaderItem!!.header.childCount
//
//                        if (doneHeaderItem!!.header.childCount == 0) {
//                            itemList.remove(doneHeaderItem)
//                            doneHeaderItem = null
//                        }
//                    }
//
//                    notifyDataSetChanged()
//                }
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (itemList[position]) {
//            is DataItem.HeaderItem -> ITEM_VIEW_TYPE_HEADER
//            is DataItem.ChildItem -> ITEM_VIEW_TYPE_ITEM
//        }
//    }
//
//    fun addTask(task: Task) {
//        currentProject.addTask(task)
//        itemList.add(0, DataItem.ChildItem(task))
//        notifyDataSetChanged()
//    }
//
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
//
///*    fun removeTask(position: Int) {
//        project.removeTaskAt(position)
//        notifyDataSetChanged()
//    }
//
//    fun swapTasks(from: Int, to: Int): Boolean {
//        val taskList: List<Task> = project.getTaskList()
//
//        if (from !in taskList.indices) {
//            return false
//        }
//
//        if (to !in taskList.indices) {
//            return false
//        }
//
//        if (from < to) {
//            for (i in from until to) {
//                Collections.swap(taskList, i, i + 1)
//            }
//        } else {
//            for (i in from downTo to + 1) {
//                Collections.swap(taskList, i, i - 1)
//            }
//        }
//
//        notifyItemMoved(from, to)
//        return true
//    }*/
//}
//
//sealed class DataItem {
//    data class ChildItem(val task: Task) : DataItem()
//
//    class HeaderItem(val header: Header) : DataItem() {
//        /*val header = Header(true, "완료됨", 0)*/
//        val childList = mutableListOf<ChildItem>()
//    }
//}
//
//data class Header(var isOpen: Boolean, val name: String, var childCount: Int)
//
//class TaskListener(val clickListener: (Task) -> Unit) {
//    fun onClick(task: Task) = clickListener(task)
//}
//
///*
//class TaskAdapter(private val project: Project, private val onClick: (Task) -> Unit) :
//    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
//    class TaskViewHolder(itemView: View, val onClick: (Task) -> Unit) :
//        RecyclerView.ViewHolder(itemView) {
//        private val checkBox = itemView.findViewById<CheckBox>(R.id.task_checkBox)
//        private val textView = itemView.findViewById<TextView>(R.id.task_text)
//        private val importantButton = itemView.findViewById<ImageButton>(R.id.important_button)
//        private var currentTask: Task? = null
//
//        init {
//            itemView.setOnClickListener {
//                currentTask?.let {
//                    onClick(it)
//                }
//            }
//
//            checkBox.setOnClickListener {
//                currentTask?.let {
//                    it.isDone = checkBox.isChecked
//                }
//            }
//
//            importantButton.setOnClickListener {
//                currentTask?.let {
//                    if (it.isImportant) {
//                        it.isImportant = false
//                        importantButton.setImageResource(R.drawable.baseline_star_border_black_24)
//                    } else {
//                        it.isImportant = true
//                        importantButton.setImageResource(R.drawable.baseline_star_24)
//                    }
//                }
//            }
//        }
//
//        fun bind(task: Task) {
//            currentTask = task
//            checkBox.isChecked = task.isDone
//            textView.text = task.name
//            if (task.isImportant) {
//                importantButton.setImageResource(R.drawable.baseline_star_24)
//            } else {
//                importantButton.setImageResource(R.drawable.baseline_star_border_black_24)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.task_item2, parent, false)
//        return TaskViewHolder(view, onClick)
//    }
//
//    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
//        holder.bind(project.getTaskList()[position])
//    }
//
//    override fun getItemCount(): Int {
//        return project.getTaskList().size
//    }
//
//    fun removeTask(position: Int) {
//        project.removeTaskAt(position)
//        notifyDataSetChanged()
//    }
//
//    fun swapTasks(from: Int, to: Int): Boolean {
//        val taskList: List<Task> = project.getTaskList()
//
//        if (from !in taskList.indices) {
//            return false
//        }
//
//        if (to !in taskList.indices) {
//            return false
//        }
//
//        if (from < to) {
//            for (i in from until to) {
//                Collections.swap(taskList, i, i + 1)
//            }
//        } else {
//            for (i in from downTo to + 1) {
//                Collections.swap(taskList, i, i - 1)
//            }
//        }
//
//        notifyItemMoved(from, to)
//        return true
//    }
//}
//*/
