package com.example.facemaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class ProjectHeaderAdapter(private val onClick: (Int) -> Unit) :
    Adapter<ProjectHeaderAdapter.ProjectHeaderItemViewHolder>() {
    private lateinit var parentContext: Context

    class ProjectHeaderItemViewHolder(itemView: View, val onClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.project_header_item_icon)
        val nameTextView: TextView = itemView.findViewById(R.id.project_header_item_name)
        val countTextView: TextView = itemView.findViewById(R.id.project_header_item_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHeaderItemViewHolder {
        parentContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_header_item, parent, false)
        return ProjectHeaderItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ProjectHeaderItemViewHolder, position: Int) {
        holder.apply {
            when (position) {
                0 -> {
                    icon.setImageResource(R.drawable.baseline_calendar_today_black_24)
                    nameTextView.text = "오늘 할 일"
                    countTextView.text = ProjectManager.getTodayTaskList().size.toString()
                }
                1 -> {
                    icon.setImageResource(R.drawable.baseline_star_24)
                    nameTextView.text = "중요"
                    countTextView.text = ProjectManager.getImportantTaskList().size.toString()
                }
                2 -> {
                    icon.setImageResource(R.drawable.baseline_calendar_today_black_24)
                    nameTextView.text = "계획된 일정"
                    countTextView.text = ""
                }
                else -> {
                    assert(false)
                }
            }
        }

        holder.itemView.setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}