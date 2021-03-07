package com.example.facemaker.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Task(
    val id: String = "",
    val projectId: String = "",
    val userId: String = "",
    var name: String = "",
    val createdDateTime: Date = Date(),
    var deadline: Date? = null, // 기한
    var notificationDateTime: Date? = null, // 알림
    var repeatCycle: String? = null // 추후 RepeatCycle 클래스 만들 것
) {
    val updatedDateTime: Date = createdDateTime
    var completionDateTime: Date? = null
    var isImportant: Boolean = false
    val stepList = mutableListOf<String>() // 단계 리스트
    val fileList = mutableListOf<String>() // 첨부 파일 리스트
    var note: String = "" // 메모

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "projectId" to projectId,
            "userId" to userId,
            "name" to name,
            "createdDateTime" to createdDateTime,
            "deadline" to deadline,
            "notificationDateTime" to notificationDateTime,
            "repeatCycle" to repeatCycle
        )
    }
}
