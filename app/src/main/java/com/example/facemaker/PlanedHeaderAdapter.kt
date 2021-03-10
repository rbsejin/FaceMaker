package com.example.facemaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.databinding.PlannedHeaderItemBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PlannedHeaderAdapter :
    RecyclerView.Adapter<PlannedHeaderAdapter.ViewHolder>() {
    class ViewHolder private constructor(val binding: PlannedHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.plannedFilter.setOnClickListener {
                PopupMenu(binding.root.context, itemView).apply {
                    menuInflater.inflate(R.menu.planned_filter_menu, menu)

                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.overdue_item -> {
                                binding.plannedFilter.text =
                                    binding.root.context.getText(R.string.overdue)
                                Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/planned/filter")
                                    .setValue(TaskFilter.OVERDUE)
                                true
                            }
                            R.id.today_item -> {
                                binding.plannedFilter.text =
                                    binding.root.context.getText(R.string.today)
                                Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/planned/filter")
                                    .setValue(TaskFilter.TODAY)
                                true
                            }
                            R.id.tomorrow_item -> {
                                binding.plannedFilter.text =
                                    binding.root.context.getText(R.string.tomorrow)
                                Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/planned/filter")
                                    .setValue(TaskFilter.TOMORROW)
                                true
                            }
                            R.id.this_week_item -> {
                                binding.plannedFilter.text =
                                    binding.root.context.getText(R.string.this_week)
                                Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/planned/filter")
                                    .setValue(TaskFilter.THIS_WEEK)
                                true
                            }
                            R.id.later_item -> {
                                binding.plannedFilter.text =
                                    binding.root.context.getText(R.string.later)
                                Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/planned/filter")
                                    .setValue(TaskFilter.LATER)
                                true
                            }
                            R.id.all_planned_item -> {
                                binding.plannedFilter.text =
                                    binding.root.context.getText(R.string.all_planned)
                                Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/planned/filter")
                                    .setValue(TaskFilter.ALL_PLANNED)
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }

            binding.groupByLayout.setOnClickListener {

            }

            binding.plannedGroupClose.setOnClickListener {
                Firebase.database.reference.child("users/${Firebase.auth.currentUser.uid}/planned/groupBy").setValue(null)
            }
        }

        fun bind(filter: TaskFilter, groupBy: TaskGroupBy) {
            when (filter) {
                TaskFilter.OVERDUE -> {
                    binding.plannedFilter.text = binding.root.context.getText(R.string.overdue)
                }
                TaskFilter.TODAY -> {
                    binding.plannedFilter.text = binding.root.context.getText(R.string.today)
                }
                TaskFilter.TOMORROW -> {
                    binding.plannedFilter.text = binding.root.context.getText(R.string.tomorrow)
                }
                TaskFilter.THIS_WEEK -> {
                    binding.plannedFilter.text = binding.root.context.getText(R.string.this_week)
                }
                TaskFilter.LATER -> {
                    binding.plannedFilter.text = binding.root.context.getText(R.string.later)
                }
                TaskFilter.ALL_PLANNED -> {
                    binding.plannedFilter.text = binding.root.context.getText(R.string.all_planned)
                }
            }

            when (groupBy) {
                null -> {
                    binding.planedGroupText.text = ""
                }
                TaskGroupBy.DATE -> {
                    binding.planedGroupText.text =
                        binding.root.context.getText(R.string.by_due_date)
                }
                TaskGroupBy.PROJECT -> {
                    binding.planedGroupText.text = binding.root.context.getText(R.string.by_project)
                }
            }
        }

        companion object {
            private lateinit var adapter: PlannedHeaderAdapter

            fun from(adapter: PlannedHeaderAdapter, parent: ViewGroup): ViewHolder {
                this.adapter = adapter

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PlannedHeaderItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    var filter: TaskFilter? = null
    var groupBy: TaskGroupBy? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(this, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        filter?.let { filter ->
            groupBy?.let { groupBy ->
                holder.bind(filter, groupBy)
            }
        }

        holder.apply {
            when (groupBy) {
                null -> {
                    binding.planedGroupText.text = ""
                    binding.groupByLayout.visibility = View.INVISIBLE
                }
                TaskGroupBy.DATE -> {
                    binding.planedGroupText.text =
                        binding.root.context.getText(R.string.by_due_date)
                    binding.groupByLayout.visibility = View.VISIBLE
                }
                TaskGroupBy.PROJECT -> {
                    binding.planedGroupText.text = binding.root.context.getText(R.string.by_project)
                    binding.groupByLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}
