package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.ChatMessage
import com.example.workly.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadChatMessages() {
        viewModelScope.launch {
            repository.getChatMessages().collectLatest { list ->
                _messages.value = list
            }
        }
    }

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    fun sendMessage(senderName: String) {
        val text = messageText.value.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _isSending.value = true
            val result = repository.sendMessage(text, senderName)
            if (!result) {
                _errorMessage.value = "Falha ao enviar mensagem"
            } else {
                _messageText.value = ""
                _errorMessage.value = null
            }
            _isSending.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
