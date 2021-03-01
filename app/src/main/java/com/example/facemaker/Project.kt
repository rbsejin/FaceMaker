package com.example.facemaker

import com.google.gson.annotations.SerializedName
import java.util.*

class Project(
    @SerializedName("id")
    val id: Int, // private set
    @SerializedName("name")
    var name: String,
    @SerializedName("createdDateTime")
    val createdDateTime: Date
) {
    @SerializedName("taskList")
    private val taskList = mutableListOf<Task>()

    fun addTask(task: Task) {
        taskList.add(task)
    }

    fun removeTask(task: Task): Boolean {
        return taskList.remove(task)
    }

    fun removeTaskForId(id: Int): Boolean {
        return taskList.removeAll { it.id == id }
    }

    fun getTaskForId(id: Int): Task? {
        return taskList.firstOrNull { it.id == id }
    }

    fun getTaskList(): List<Task> {
        return taskList
    }

    fun createId(): Int {
        var maxId = 0

        for (project in ProjectManager.getProjectList()) {
            for (task in project.getTaskList()) {
                maxId = Math.max(maxId, task.id)
            }
        }

        return maxId + 1
    }

    fun removeTaskAt(position: Int): Boolean {
        return taskList.removeAt(position) != null
    }
}