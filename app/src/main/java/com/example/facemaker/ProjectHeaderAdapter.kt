package com.example.facemaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class ProjectHeaderAdapter(val itemList: MutableList<SmartListHeader>, private val onClick: (Int) -> Unit) :
    Adapter<RecyclerView.ViewHolder>() {
    private lateinit var parentContext: Context
//    private val itemList = mutableListOf<SmartListHeader>(
//        SmartListHeader("오늘 할 일", R.drawable.ic_wb_sunny_24px),
//        SmartListHeader("작업들", R.drawable.ic_task_24px),
//    )

//    private val smartLists = mutableListOf<SmartListHeader>(
//        SmartListHeader("중요", R.drawable.ic_star_border_purple500_24px),
//        SmartListHeader("계획된 일정", R.drawable.ic_calendar_today_24px),
//        SmartListHeader("모두", R.drawable.ic_360_24px),
//        SmartListHeader("완료", R.drawable.ic_check_circle_24px)
//    )

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
//                    when (position) {
//                        0 -> {
//                            nameTextView.text = "오늘 할 일"
//                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
//                                R.drawable.ic_wb_sunny_24px,
//                                0,
//                                0,
//                                0
//                            )
//                            countTextView.visibility = View.INVISIBLE
//                        }
//                        1 -> {
//                            nameTextView.text = "중요"
//                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
//                                R.drawable.ic_star_border_purple500_24px,
//                                0,
//                                0,
//                                0
//                            )
//                            countTextView.visibility = View.INVISIBLE
//
//                            holder.itemView.visibility = if (isImportantVisible) {
//                                View.VISIBLE
//                            } else {
//                                View.INVISIBLE
//                            }
//                        }
//                        2 -> {
//                            nameTextView.text = "계획된 일정"
//                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
//                                R.drawable.ic_calendar_today_24px,
//                                0,
//                                0,
//                                0
//                            )
//
//                            countTextView.visibility = View.INVISIBLE
//
//                            holder.itemView.visibility = if (isPlannedVisible) {
//                                View.VISIBLE
//                            } else {
//                                View.INVISIBLE
//                            }
//                        }
//                        3 -> {
//                            nameTextView.text = "모두"
//                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
//                                R.drawable.ic_360_24px,
//                                0,
//                                0,
//                                0
//                            )
//                            countTextView.visibility = View.INVISIBLE
//
//                            holder.itemView.visibility = if (isAllVisible) {
//                                View.VISIBLE
//                            } else {
//                                View.INVISIBLE
//                            }
//                        }
//                        4 -> {
//                            nameTextView.text = "완료"
//                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
//                                R.drawable.ic_check_circle_24px,
//                                0,
//                                0,
//                                0
//                            )
//                            countTextView.visibility = View.INVISIBLE
//
//                            holder.itemView.visibility = if (isCompletedVisible) {
//                                View.VISIBLE
//                            } else {
//                                View.INVISIBLE
//                            }
//                        }
//                        5 -> {
//                            nameTextView.text = "작업들"
//                            nameTextView.setCompoundDrawablesWithIntrinsicBounds(
//                                R.drawable.ic_task_24px,
//                                0,
//                                0,
//                                0
//                            )
//                            countTextView.visibility = View.INVISIBLE
//                        }
//                        else -> {
//                            if (BuildConfig.DEBUG) {
//                                error("invalid")
//                            }
//                        }
//                    }

                    nameTextView.text = itemList[position].name
                    nameTextView.setCompoundDrawablesWithIntrinsicBounds(
                        itemList[position].icon,
                        0,
                        0,
                        0
                    )
                    countTextView.visibility = View.INVISIBLE
                }
            }
        }

        holder.itemView.setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemList.size) 1 else 0

    }

    companion object {
        private var itemCount = 7
        private var isAllVisible = true
        private var isImportantVisible = true
        private var isPlannedVisible = true
        private var isCompletedVisible = true

        fun setAllVisible(isVisible: Boolean) {
            if (isAllVisible != isVisible) {
                if (isVisible) {
                    ++itemCount
                } else {
                    --itemCount
                }
            }

            isAllVisible = isVisible
        }

        fun setImportantVisible(isVisible: Boolean) {
            if (isImportantVisible != isVisible) {
                if (isVisible) {
                    ++itemCount
                } else {
                    --itemCount
                }
            }

            isImportantVisible = isVisible
        }

        fun setPlannedVisible(isVisible: Boolean) {
            if (isPlannedVisible != isVisible) {
                if (isVisible) {
                    ++itemCount
                } else {
                    --itemCount
                }
            }

            isPlannedVisible = isVisible
        }

        fun setCompletedVisible(isVisible: Boolean) {
            if (isCompletedVisible != isVisible) {
                if (isVisible) {
                    ++itemCount
                } else {
                    --itemCount
                }
            }

            isCompletedVisible = isVisible
        }
    }
}

data class SmartListHeader(val key: String = "", val name: String = "", val icon: Int = 0, var isVisible: Boolean = true)
