package com.example.facemaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.SearchTaskItemBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class SearchTaskAdapter(private val clickListener: TaskListener) :
    RecyclerView.Adapter<SearchTaskAdapter.ViewHolder>() {
    var itemList = listOf<Task>()

    class ViewHolder private constructor(private val binding: SearchTaskItemBinding) :
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
                val binding = SearchTaskItemBinding.inflate(layoutInflater, parent, false)
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
}