package com.example.facemaker

import com.google.gson.annotations.SerializedName
import java.util.*

data class Task(
    @SerializedName("id")
    val id: Long, // private set
    @SerializedName("projectId")
    var projectId: Long,
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
    var isDone = false // 완수 여부
    val stepList = mutableListOf<String>() // 단계
    val fileList = mutableListOf<String>() // 첨부 파일 리스트
    var note = "" // 메모
    //val completedDateTime : Date? = null // 완료된 날짜
}