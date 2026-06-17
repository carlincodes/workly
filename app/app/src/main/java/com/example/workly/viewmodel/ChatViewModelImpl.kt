package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.ChatMessage
import com.example.workly.repository.ChatRepository
import com.example.workly.service.ChatConversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

   
                             
                                                     
   
class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentChatId = MutableStateFlow("")
    val currentChatId: StateFlow<String> = _currentChatId

    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations

       
                                                            
       
    fun initializeChat(userId1: String, userId2: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val chatId = chatRepository.getOrCreateChat(userId1, userId2)
                _currentChatId.value = chatId
                
                                                   
                chatRepository.observeMessages(chatId).collect { messageList ->
                    _messages.value = messageList
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao inicializar chat"
            } finally {
                _isLoading.value = false
            }
        }
    }

       
                         
       
    fun sendMessage(
        text: String,
        senderId: String,
        senderName: String,
        senderImage: String = ""
    ) {
        if (text.isBlank() || _currentChatId.value.isEmpty()) {
            _error.value = "Mensagem ou chat inválido"
            return
        }

        viewModelScope.launch {
            try {
                val message = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderId = senderId,
                    senderName = senderName,
                    senderImage = senderImage,
                    text = text,
                    timestamp = System.currentTimeMillis(),
                    messageType = "text"
                )

                val success = chatRepository.sendMessage(_currentChatId.value, message)
                if (!success) {
                    _error.value = "Erro ao enviar mensagem"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao enviar mensagem"
            }
        }
    }

       
                                   
       
    fun loadUserConversations(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                chatRepository.getUserChats(userId).collect { convs ->
                    _conversations.value = convs
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar conversas"
            } finally {
                _isLoading.value = false
            }
        }
    }

       
                     
       
    fun deleteConversation(chatId: String) {
        viewModelScope.launch {
            try {
                val success = chatRepository.deleteChat(chatId)
                if (!success) {
                    _error.value = "Erro ao deletar conversa"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao deletar conversa"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
