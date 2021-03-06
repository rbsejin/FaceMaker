package com.example.facemaker.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String = "",
    var name: String = "",
    val email: String = "",
    /*var photoUrl: Uri? = null,*/
    var emailVerified: Boolean = false,
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            /*"photoUrl" to photoUrl,*/
            "emailVerified" to emailVerified,
        )
    }
}
