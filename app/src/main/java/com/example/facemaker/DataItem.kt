package com.example.facemaker

import com.example.facemaker.data.Task

sealed class DataItem {
    data class ChildItem(val task: Task) : DataItem()

    class HeaderItem(val header: Header) : DataItem() {
        val childList = mutableListOf<ChildItem>()
    }
}

data class Header(var isOpen: Boolean, val name: String, var childCount: Int)
