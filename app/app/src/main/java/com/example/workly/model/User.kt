package com.example.workly.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val specialty: String = "", // Para prestadores
    val photoUrl: String = "",
    val isProvider: Boolean = false
)
