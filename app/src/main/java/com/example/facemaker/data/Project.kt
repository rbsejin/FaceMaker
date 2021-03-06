package com.example.facemaker.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Project(
    val id: String = "",
    val hostUserId: String = "",
    var name: String = "",
    val createdDate: Date = Date()
) {
    var updatedDate: Date = createdDate

    @Exclude
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "hostUserId" to hostUserId,
            "name" to name,
            "createdDate" to createdDate
        )
    }
}
