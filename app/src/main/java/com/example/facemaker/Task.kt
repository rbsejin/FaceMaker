package com.example.facemaker

import com.google.gson.annotations.SerializedName
import java.util.*

data class Task(
    @SerializedName("id")
    val id: Int, // private set
    @SerializedName("projectId")
    var projectId: Int,
    @SerializedName("content")
    var content: String,
    @SerializedName("createdDateTime")
    val createdDateTime: Date, // private set
    @SerializedName("deadline")
    var deadline: Date? = null,
    @SerializedName("notificationDateTime")
    var notificationDateTime: Date? = null,
    @SerializedName("repeatCycle")
    var repeatCycle: String? = null // 추후 RepeatCycle 클래스 만들 것
) {
    @SerializedName("isDone")
    var isDone = false // 완수 여부
    @SerializedName("stepList")
    val stepList = mutableListOf<String>() // 단계
    @SerializedName("fileList")
    val fileList = mutableListOf<String>() // 첨부 파일 리스트
    @SerializedName("note")
    var note = "" // 메모
    //val completedDateTime : Date? = null // 완료된 날짜
    @SerializedName("todayTaskDate")
    var todayTaskDate: Date? = null // 오늘 할 일
    @SerializedName("isImportant")
    var isImportant: Boolean = false
}