package com.example.facemaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.TaskItemBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class TaskAdapter(
    private val clickListener: TaskListener /*private val onClick: (Task) -> Unit*/
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var itemList = listOf<Task>()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task: Task = itemList[position]
        holder.bind(task, clickListener)

        holder.getCheckButton().setOnClickListener {
            if (task.completionDateTime == null) {
                task.completionDateTime = Calendar.getInstance().time
            } else {
                task.completionDateTime = null
            }
            Firebase.database.reference.child("tasks").child(task.id).setValue(task)
        }

        holder.getImportantButton().setOnClickListener {
            task.isImportant = !task.isImportant
            Firebase.database.reference.child("tasks").child(task.id).setValue(task)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun isItemMoved(fromPosition: Int, toPosition: Int): Boolean {
        return fromPosition < itemList.size && toPosition < itemList.size
    }
}

class TaskListener(val clickListener: (Task) -> Unit) {
    fun onClick(task: Task) = clickListener(task)
}
