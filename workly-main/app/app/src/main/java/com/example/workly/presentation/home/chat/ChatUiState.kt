package com.example.workly.presentation.chat

import com.example.workly.model.ChatMessage

data class ChatUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val errorMessage: String? = null,
    val messageInput: String = "",
    val chatPartnerName: String = "Prestador"
) {
    val isChatEmpty: Boolean get() = !isLoading && messages.isEmpty() && errorMessage == null
}