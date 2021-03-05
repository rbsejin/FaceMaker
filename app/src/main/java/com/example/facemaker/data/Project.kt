package com.example.facemaker.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Project(
    val id: String = "",
    val userId: String = "",
    var name: String = "",
    val createdDate: Date = Date()
) {
    var updatedDate: Date = createdDate

    @Exclude
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "name" to name,
            "createdDate" to createdDate
        )
    }
}
