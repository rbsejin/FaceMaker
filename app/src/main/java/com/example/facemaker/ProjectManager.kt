package com.example.facemaker

object ProjectManager {
    private val projectList = mutableListOf<Project>()

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