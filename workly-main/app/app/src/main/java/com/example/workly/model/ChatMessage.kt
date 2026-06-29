package com.example.workly.model

import com.google.firebase.firestore.PropertyName

   
                                    
                                                                
   
data class ChatMessage(
    @PropertyName("id")
    val id: String = "",
    
    @PropertyName("senderId")
    val senderId: String = "",
    
    @PropertyName("senderName")
    val senderName: String = "",
    
    @PropertyName("senderImage")
    val senderImage: String = "",
    
    @PropertyName("text")
    val text: String = "",
    
    @PropertyName("timestamp")
    val timestamp: Long = 0L,
    
    @PropertyName("messageType")
    val messageType: String = "text"                         
) {
    constructor() : this(
        id = "",
        senderId = "",
        senderName = "",
        senderImage = "",
        text = "",
        timestamp = 0L,
        messageType = "text"
    )
}
