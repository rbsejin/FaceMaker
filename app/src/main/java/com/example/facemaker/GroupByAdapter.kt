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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class GroupByAdapter(
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

    private val itemList = mutableListOf<DataItem>()
    var groupBy: TaskGroupBy = TaskGroupBy.PROJECT
    var completionFilter: Boolean = false

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
                val task: Task = childItem.task
                holder.bind(childItem.task, clickListener)

                holder.getCheckButton().setOnClickListener {
                    val task = task
                    if (task.completionDateTime == null) {
                        task.completionDateTime = Calendar.getInstance().time
                    } else {
                        task.completionDateTime = null
                    }
                    Firebase.database.reference.child("tasks").child(task.id).setValue(task)
                }

                holder.getImportantButton().setOnClickListener {
                    val task = task
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

    fun updateItemList(list: MutableList<Task>) {
        // 기간 또는 프로젝트 별로 그룹핑
        itemList.clear()

        Firebase.database.reference.child("projects").get().addOnSuccessListener { dataSnapshot ->
            list.sortBy { it.projectId }

            val projectMap: Map<String, Project> =
                dataSnapshot.getValue<Map<String, Project>>() ?: return@addOnSuccessListener

            for (i in 0 until list.size) {
                val task = list[i]

                // 기간 해더 생성 및 추가
                var headerItem: DataItem.HeaderItem? = null

                if (i == 0 || (task.projectId) != list[i - 1].projectId) {
                    val project = projectMap[task.projectId] ?: return@addOnSuccessListener
                    val headerName = project.name
                    val headerItem = DataItem.HeaderItem(Header(true, headerName, 0))
                    itemList.add(headerItem)
                }

                itemList.add(DataItem.ChildItem(task))
                headerItem?.apply { ++header.childCount }
            }

            notifyDataSetChanged()
        }
    }
}
