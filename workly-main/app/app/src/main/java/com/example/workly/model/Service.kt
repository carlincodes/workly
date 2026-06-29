package com.example.workly.model

import com.google.firebase.firestore.DocumentId

data class Service(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val jobType: String = "",
    val clientId: String = "",
    val status: String = "OPEN"
)
