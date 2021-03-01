package com.example.facemaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ImportantTaskAdapter(
    var importantTaskList: MutableList<Task>,
    private val onClick: (Task) -> Unit
) :
    RecyclerView.Adapter<ImportantTaskAdapter.TaskViewHolder>() {
    class TaskViewHolder(itemView: View, val onClick: (Task) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val checkBox = itemView.findViewById<CheckBox>(R.id.task_checkBox)
        private val textView = itemView.findViewById<TextView>(R.id.task_text)
        val importantButton = itemView.findViewById<ImageButton>(R.id.important_button)
        var currentTask: Task? = null

        init {
            itemView.setOnClickListener {
                currentTask?.let {
                    onClick(it)
                }
            }

            val checkBox: CheckBox = itemView.findViewById(R.id.task_checkBox)
            checkBox.setOnClickListener {
                currentTask?.let {
                    it.isDone = checkBox.isChecked
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
        holder.bind(importantTaskList[position])

        holder.importantButton.setOnClickListener {
            holder.currentTask?.let {
                if (it.isImportant) {
                    it.isImportant = false
                    holder.importantButton.setImageResource(R.drawable.baseline_star_border_black_24)
                } else {
                    it.isImportant = true
                    holder.importantButton.setImageResource(R.drawable.baseline_star_24)
                }
            }

            importantTaskList.removeAt(position)

            // 기한 설정 제거하면 반복도 같이 제거
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return importantTaskList.size
    }

    fun removeImportantTask(position: Int) {
        importantTaskList[position].isImportant = false
        importantTaskList.removeAt(position)
        notifyDataSetChanged()
    }

    fun swapTasks(from: Int, to: Int): Boolean {
        if (from !in importantTaskList.indices) {
            return false
        }

        if (to !in importantTaskList.indices) {
            return false
        }

        if (from < to) {
            for (i in from until to) {
                Collections.swap(importantTaskList, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(importantTaskList, i, i - 1)
            }
        }

        notifyItemMoved(from, to)
        return true
    }
}