package com.example.facemaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.data.Project
import com.example.facemaker.databinding.ProjectItemBinding

class ProjectAdapter(
    private val projects: List<Project>,
    private val onClick: (Project) -> Unit
) :
    RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {
    class ViewHolder private constructor(
        private val binding: ProjectItemBinding,
        private val onClick: (Project) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentProject: Project? = null

        init {
            itemView.setOnClickListener {
                currentProject?.let {
                    onClick(it)
                }
            }
        }

        fun bind(project: Project) {
            currentProject = project
            binding.apply {
                projectItemName.text = project.name
                projectItemChildCount.text = "0"//tasks.count { !it.isDone }.toString()
            }
        }

        companion object {
            fun from(projectAdapter: ProjectAdapter, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ProjectItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, projectAdapter.onClick)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(this, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    fun removeProject(adapterPosition: Int) {
//        ProjectManager.removeAt(adapterPosition)
    }

    fun swapProejcts(from: Int, to: Int): Boolean {
//        val proejctList: List<Project> = ProjectManager.getProjectList()
//
//        if (from !in proejctList.indices) {
//            return false
//        }
//
//        if (to !in proejctList.indices) {
//            return false
//        }
//
//        if (from < to) {
//            for (i in from until to) {
//                Collections.swap(proejctList, i, i + 1)
//            }
//        } else {
//            for (i in from downTo to + 1) {
//                Collections.swap(proejctList, i, i - 1)
//            }
//        }
//
//        notifyItemMoved(from, to)
        return true
    }
}
