package com.example.workly.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    @DocumentId
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)
