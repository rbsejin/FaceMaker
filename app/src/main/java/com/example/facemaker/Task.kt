package com.example.facemaker

import java.util.*

data class Task(
    val id: Long, // private set
    var projectId: Long,
    var content: String,
    val createdDateTime: Date, // private set
    var deadline: Date? = null,
    var notificationDateTime: Date? = null,
    var repeatCycle: String? = null // 추후 RepeatCycle 클래스 만들 것
) {
    var isDone = false // 완수 여부
    val stepList = mutableListOf<String>() // 단계
    val fileList = mutableListOf<String>() // 첨부 파일 리스트
    var note = "" // 메모
    //val completedDateTime : Date? = null // 완료된 날짜
}