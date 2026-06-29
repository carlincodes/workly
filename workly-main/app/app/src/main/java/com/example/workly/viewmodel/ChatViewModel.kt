package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.ChatMessage
import com.example.workly.repository.ChatRepository
import com.example.workly.presentation.chat.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository = ChatRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                chatRepository.getChatMessages().collect { messageList ->
                    _uiState.update { it.copy(messages = messageList) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Erro ao carregar mensagens") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onMessageTextChanged(text: String) {
        _uiState.update { it.copy(messageInput = text) }
    }

    fun onSendMessageClicked() {
        val state = _uiState.value
        if (state.messageInput.isBlank()) return
        
        viewModelScope.launch {
            try {
                val success = chatRepository.sendMessage(state.messageInput, "Usuário")
                if (success) {
                    _uiState.update { it.copy(messageInput = "") }
                } else {
                    _uiState.update { it.copy(errorMessage = "Erro ao enviar mensagem") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Erro ao enviar mensagem") }
            }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
