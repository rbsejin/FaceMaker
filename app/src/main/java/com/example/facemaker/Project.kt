package com.example.facemaker

object Project/*(
    // 일단은 Project 별로 id를 두었지만 모든 task id 를 다르게 해야하지 않을까?
    val id: Long, // private set
    var content: String,
    val createdDateTime: Date
)*/ {
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

    fun getLastId(): Long? {
        if (taskList.size == 0) {
            return null
        }

        return taskList[taskList.size - 1].id
    }
}