package com.example.facemaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class ProjectHeaderAdapter() :
    Adapter<ProjectHeaderAdapter.ProjectHeaderItemViewHolder>() {
    private lateinit var parentContext: Context

    class ProjectHeaderItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.project_header_item_icon)
        val contentTextView: TextView = itemView.findViewById(R.id.project_header_item_content)
        val countTextView: TextView = itemView.findViewById(R.id.project_header_item_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHeaderItemViewHolder {
        parentContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_header_item, parent, false)
        return ProjectHeaderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectHeaderItemViewHolder, position: Int) {
        holder.apply {
            when (position) {
                0 -> {
                    icon.setImageResource(R.drawable.baseline_calendar_today_black_24)
                    contentTextView.text = "오늘 할 일"
                    countTextView.text = ""
                }
                1 -> {
                    icon.setImageResource(R.drawable.baseline_calendar_today_black_24)
                    contentTextView.text = "이번 주 할 일"
                    countTextView.text = ""
                }
                2 -> {
                    icon.setImageResource(R.drawable.baseline_calendar_today_black_24)
                    contentTextView.text = "계획된 일정"
                    countTextView.text = ""
                }
                else -> {
                    assert(false)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}