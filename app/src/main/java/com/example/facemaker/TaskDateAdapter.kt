package com.example.facemaker

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class TaskDateAdapter :
    Adapter<TaskDateAdapter.TaskDateViewHolder>() {
    private val taskDateList: List<TaskDateItem> = listOf(
        TaskDateItem(R.drawable.baseline_notifications_none_black_24, "미리 알림"),
        TaskDateItem(R.drawable.baseline_calendar_today_black_24, "기한 설정"),
        TaskDateItem(R.drawable.baseline_repeat_black_24, "반복")
    )

    private lateinit var parentContext: Context

    class TaskDateViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.task_date_item_icon)
        val content: TextView = itemView.findViewById(R.id.task_date_item_content)
        val close: ImageButton = itemView.findViewById(R.id.task_date_item_close)
        var currentTaskDateItem: TaskDateItem? = null

        init {
            itemView.setOnClickListener {

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
        parentContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_date_item, parent, false)
        return TaskDateViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskDateViewHolder, position: Int) {
        holder.bind(taskDateList[position])

        holder.itemView.setOnClickListener {
            val popup = PopupMenu(parentContext, holder.itemView)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.date_item_menu, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.today_later_item -> {
                        Toast.makeText(parentContext, "today", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.tomorrow_item -> {
                        true
                    }
                    R.id.next_week_item -> {
                        true
                    }
                    R.id.direct_selection_item -> {
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}

data class TaskDateItem(
    @DrawableRes
    val image: Int?,
    var content: String,
)