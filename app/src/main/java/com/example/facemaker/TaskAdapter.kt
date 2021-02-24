package com.example.facemaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class TaskAdapter(private val project: Project, private val onClick: (Task) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    class TaskViewHolder(itemView: View, val onClick: (Task) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val checkBox = itemView.findViewById<CheckBox>(R.id.task_checkBox)
        private val textView = itemView.findViewById<TextView>(R.id.task_text)
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
        }

        fun bind(task: Task) {
            currentTask = task
            checkBox.isChecked = task.isDone
            textView.text = task.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
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