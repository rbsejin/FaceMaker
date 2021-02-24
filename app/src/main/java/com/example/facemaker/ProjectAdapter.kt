package com.example.facemaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectAdapter(private val onClick: (Project) -> Unit) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {
    class ProjectViewHolder(itemView: View, val onClick: (Project) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val iconImageView = itemView.findViewById<ImageView>(R.id.project_item_icon)
        private val contentTextView = itemView.findViewById<TextView>(R.id.project_item_content)
        private val taskCountTextView =
            itemView.findViewById<TextView>(R.id.project_item_task_count)
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
            contentTextView.text = project.content
            taskCountTextView.text = project.getTaskList().size.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item, parent, false)
        return ProjectViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(ProjectManager.getProjectList()[position])
    }

    override fun getItemCount(): Int {
        return ProjectManager.getProjectList().size
    }

    fun removeProject(adapterPosition: Int) {
        ProjectManager.removeAt(adapterPosition)
    }
}
