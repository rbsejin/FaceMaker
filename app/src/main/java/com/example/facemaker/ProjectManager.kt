package com.example.facemaker

import java.util.*

object ProjectManager {
    private val projectList = mutableListOf<Project>(
        Project(1, "안드로이드 프로젝트", Calendar.getInstance().time)
    )

    fun addProject(project: Project) {
        projectList.add(project)
    }

    fun removeProject(project: Project): Boolean {
        return projectList.remove(project)
    }

    fun removeProjectForId(id : Long): Boolean {
        return projectList.removeAll { it.id == id }
    }

    fun getProjectForId(id: Long): Project? {
        return projectList.firstOrNull { it.id == id }
    }

    fun getProjectList() : List<Project> {
        return projectList
    }

    fun createId(): Long {
        if (projectList.size == 0) {
            return 1
        }

        return projectList[projectList.size - 1].id + 1
    }
}