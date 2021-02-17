package com.example.facemaker

import com.google.gson.annotations.SerializedName
import java.util.*

class Project(
    @SerializedName("id")
    val id: Long, // private set
    @SerializedName("content")
    var content: String,
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

    fun removeTaskForId(id: Long): Boolean {
        return taskList.removeAll { it.id == id }
    }

    fun getTaskForId(id: Long): Task? {
        return taskList.firstOrNull { it.id == id }
    }

    fun getTaskList(): List<Task> {
        return taskList
    }

    fun createId(): Long {
        if (taskList.size == 0) {
            return 1
        }

        return taskList[taskList.size - 1].id + 1
    }
}