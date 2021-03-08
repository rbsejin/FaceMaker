package com.example.facemaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.HeaderItemBinding
import com.example.facemaker.databinding.TaskItemBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class DoneTaskAdapter(private val clickListener: TaskListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList = mutableListOf<DataItem>()

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

    private var doneHeader: DataItem.HeaderItem? = null

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

    fun updateItemList(list: List<Task>) {
        if (list.isEmpty()) {
            doneHeader = null
            itemList.clear()
            return
        }

        if (doneHeader == null) {
            doneHeader = DataItem.HeaderItem(Header(true, "완료됨", list.size))
        } else {
            doneHeader!!.header.childCount = list.size
            doneHeader!!.childList.clear()
        }

        itemList.clear()
        itemList.add(doneHeader!!)

        val taskItemList = (list.map { DataItem.ChildItem(it) }) as MutableList<DataItem.ChildItem>

        if (doneHeader!!.header.isOpen) {
            itemList.addAll(taskItemList)
        } else {
            doneHeader!!.childList.addAll(taskItemList)
        }
        notifyDataSetChanged()
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
