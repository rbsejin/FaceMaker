package com.example.facemaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class TodayTaskAdapter(var todayTaskList: MutableList<Task>, private val onClick: (Task) -> Unit) :
    RecyclerView.Adapter<TodayTaskAdapter.TaskViewHolder>() {
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

            val checkBox: CheckBox = itemView.findViewById(R.id.task_checkBox)
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
        holder.bind(todayTaskList[position])
    }

    override fun getItemCount(): Int {
        return todayTaskList.size
    }

    fun removeTodayTask(position: Int) {
        todayTaskList[position].todayTaskDate = null
        todayTaskList.removeAt(position)
        notifyDataSetChanged()
    }

    fun swapTasks(from: Int, to: Int): Boolean {
        if (from !in todayTaskList.indices) {
            return false
        }

        if (to !in todayTaskList.indices) {
            return false
        }

        if (from < to) {
            for (i in from until to) {
                Collections.swap(todayTaskList, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(todayTaskList, i, i - 1)
            }
        }

        notifyItemMoved(from, to)
        return true
    }
}