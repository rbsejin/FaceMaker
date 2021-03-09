package com.example.facemaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class ProjectHeaderAdapter(private val onClick: (Int) -> Unit) :
    Adapter<RecyclerView.ViewHolder>() {
    private lateinit var parentContext: Context

    class ProjectHeaderItemViewHolder(itemView: View, val onClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.project_item_name)
        val countTextView: TextView = itemView.findViewById(R.id.project_item_child_count)
    }

    class DividerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.divider_line, parent, false)
            return DividerItemViewHolder(view)
        }

        parentContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item, parent, false)
        return ProjectHeaderItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProjectHeaderItemViewHolder -> {
                holder.apply {
                    when (position) {
                        0 -> {
                            nameTextView.text = "오늘 할 일"
                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_wb_sunny_24px,
                                0,
                                0,
                                0
                            )
                            countTextView.text = "0"
                        }
                        1 -> {
                            nameTextView.text = "중요"
                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_star_border_purple500_24px,
                                0,
                                0,
                                0
                            )
                            countTextView.text = "0"
                        }
                        2 -> {
                            nameTextView.text = "계획된 일정"
                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_calendar_today_24px,
                                0,
                                0,
                                0
                            )
                            countTextView.text = "0"
                        }
                        else -> {
                            if (BuildConfig.DEBUG) {
                                error("invalid")
                            }
                        }
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return HEADER_ITEM_COUNT
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == HEADER_ITEM_COUNT - 1) 1 else 0

    }

    private companion object {
        private const val HEADER_ITEM_COUNT = 4
    }
}