package com.example.facemaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val onClick: (Task) -> Unit) :
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
        holder.bind(Project.getTaskList()[position])
    }

    override fun getItemCount(): Int {
        return Project.getTaskList().size
    }
}