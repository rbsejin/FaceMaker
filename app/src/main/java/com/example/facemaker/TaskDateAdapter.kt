package com.example.facemaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class TaskDateAdapter() :
    Adapter<TaskDateAdapter.TaskDateViewHolder>() {
    private val taskDateList: List<TaskDateItem> = listOf(
        TaskDateItem(R.drawable.baseline_notifications_none_black_24, "미리 알림"),
        TaskDateItem(R.drawable.baseline_calendar_today_black_24, "기한 설정"),
        TaskDateItem(R.drawable.baseline_repeat_black_24, "반복")
    )

    class TaskDateViewHolder(itemView: View, onClick: (TaskDateItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.task_date_item_icon)
        val content: TextView = itemView.findViewById(R.id.task_date_item_content)
        val close: ImageButton = itemView.findViewById(R.id.task_date_item_close)
        var currentTaskDateItem: TaskDateItem? = null

        init {
            itemView.setOnClickListener {
                currentTaskDateItem?.let {
                    onClick(it)
                }
            }

            close.visibility = View.INVISIBLE
        }

        fun bind(taskDateItem: TaskDateItem) {
            currentTaskDateItem = taskDateItem

            content.text = taskDateItem.content
            if (taskDateItem.image != null) {
                icon.setImageResource(taskDateItem.image)
            } else {
                icon.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskDateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_date_item, parent, false)
        return TaskDateViewHolder(view) {taskDateItem -> adapterOnClick(taskDateItem) }
    }

    override fun onBindViewHolder(holder: TaskDateViewHolder, position: Int) {
        holder.bind(taskDateList[position])
    }

    override fun getItemCount(): Int {
        return 3
    }

    fun adapterOnClick(taskDateItem: TaskDateItem) {
        taskDateItem?.let {
            // contextMenu 생성
            // 클릭시

            notifyDataSetChanged()
        }

        // close 클릭시 원상복구
    }
}

data class TaskDateItem(
    @DrawableRes
    val image: Int?,
    var content: String,
)