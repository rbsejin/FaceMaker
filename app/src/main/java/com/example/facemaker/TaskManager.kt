package com.example.facemaker

object TaskManager {
    private val taskList = mutableListOf<Task>()

    fun addTask(task: Task) {
        taskList.add(taskList.size, task)
    }

    fun removeTask(task: Task): Boolean {
        return taskList.remove(task)
    }

    fun getTaskForId(id: Long): Task? {
        return taskList.firstOrNull { it.id == id }
    }

    fun getTaskList() : List<Task> {
        return taskList
    }

    fun getLastId(): Long? {
        if (taskList.size == 0) {
            return null
        }

        return taskList[taskList.size - 1].id
    }
}