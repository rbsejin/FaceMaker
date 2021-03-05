//package com.example.facemaker
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.facemaker.data.Project
//import java.util.*
//
//class ProjectAdapter(private val onClick: (Project) -> Unit) :
//    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {
//    class ProjectViewHolder(itemView: View, val onClick: (Project) -> Unit) :
//        RecyclerView.ViewHolder(itemView) {
//        private val iconImageView = itemView.findViewById<ImageView>(R.id.project_item_icon)
//        private val nameTextView = itemView.findViewById<TextView>(R.id.project_item_name)
//        private val taskCountTextView =
//            itemView.findViewById<TextView>(R.id.project_item_task_count)
//        private var currentProject: Project? = null
//
//        init {
//            itemView.setOnClickListener {
//                currentProject?.let {
//                    onClick(it)
//                }
//            }
//        }
//
//        fun bind(project: Project) {
//            currentProject = project
//            nameTextView.text = project.name
//            taskCountTextView.text = project.getTaskList().count { !it.isDone}.toString()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.project_item, parent, false)
//        return ProjectViewHolder(view, onClick)
//    }
//
//    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
//        holder.bind(ProjectManager.getProjectList()[position])
//    }
//
//    override fun getItemCount(): Int {
//        return ProjectManager.getProjectList().size
//    }
//
//    fun removeProject(adapterPosition: Int) {
//        ProjectManager.removeAt(adapterPosition)
//    }
//
//    fun swapProejcts(from: Int, to: Int): Boolean {
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
//        return true
//    }
//}
