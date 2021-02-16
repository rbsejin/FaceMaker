package com.example.facemaker

import java.util.*

data class Task(
    val id: Long, // private set
    var content: String,
    val createdDateTime: Date, // private set
    var deadline: Date? = null,
    var notificationDateTime: Date? = null,
    var repeatCycle: RepeatCycle? = null
) {
    var isDone = false // 완수 여부
    val stepList = mutableListOf<String>() // 단계
    val fileList = mutableListOf<String>() // 첨부 파일 리스트
    var note = "" // 메모
    //val completedDateTime : Date? = null // 완료된 날짜
}