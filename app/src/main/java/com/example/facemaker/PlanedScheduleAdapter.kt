package com.example.facemaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.databinding.HeaderItemBinding
import com.example.facemaker.databinding.TaskItemBinding
import java.text.SimpleDateFormat
import java.util.*

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class PlanedScheduleAdapter(
    private val clickListener: TaskListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        private var currentTask: Task? = null

        init {
            binding.importantButton.setOnClickListener {
                currentTask?.let {
                    it.isImportant = !it.isImportant
                    binding.task = currentTask
                }
            }
        }

        fun bind(item: Task, clickListener: TaskListener) {
            currentTask = item
            binding.task = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        fun getCheckButton(): ToggleButton {
            return binding.checkButton
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    private val itemList = mutableListOf<DataItem>()
    private val taskList = mutableListOf<Task>()

    init {
        for (project in ProjectManager.getProjectList()) {
            val list = project.getTaskList().filter {it.deadline != null}
            taskList.addAll(list)
        }
        
        taskList.sortBy {
            it.deadline
        }

        val dateList = mutableListOf<Date?>()

        var headerItem: DataItem.HeaderItem? = null

        for (i in 0 until taskList.size) {
            val task = taskList[i]

            val calendar = Calendar.getInstance()
            calendar.time = task.deadline ?: continue

            val dateFormat = SimpleDateFormat("M월 d일 (EE)")

            if (i == 0 || dateFormat.format(task.deadline) != dateFormat.format(taskList[i - 1].deadline)) {
                val headerName = dateFormat.format(task.deadline)
                headerItem = DataItem.HeaderItem(Header(true, headerName, 0))
                itemList.add(headerItem)
            }

            itemList.add(DataItem.ChildItem(task))
            headerItem?.apply { ++header.childCount}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
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
}
