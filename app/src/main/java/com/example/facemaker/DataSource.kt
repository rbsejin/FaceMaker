package com.example.facemaker

import android.content.Context

class DataSource (private val context : Context) {
    fun getTaskList() : Array<String> {
        return context.resources.getStringArray(R.array.task_array)
    }
}