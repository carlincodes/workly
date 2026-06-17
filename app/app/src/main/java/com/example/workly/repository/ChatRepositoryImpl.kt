package com.example.workly.repository

import com.example.workly.model.ChatMessage
import com.example.workly.service.ChatService
import com.example.workly.service.ChatConversation
import kotlinx.coroutines.flow.Flow

   
                             
                                                           
   
class ChatRepository(private val chatService: ChatService) {

    suspend fun sendMessage(chatId: String, message: ChatMessage): Boolean {
        val result = chatService.sendMessage(chatId, message)
        if (result) {
                                                
            chatService.updateLastMessage(chatId, message.text)
        }
        return result
    }

    fun observeMessages(chatId: String): Flow<List<ChatMessage>> {
        return chatService.observeMessages(chatId)
    }

    suspend fun getOrCreateChat(userId1: String, userId2: String): String {
        return chatService.getOrCreateChat(userId1, userId2)
    }

    fun getUserChats(userId: String): Flow<List<ChatConversation>> {
        return chatService.getUserChats(userId)
    }

    suspend fun deleteChat(chatId: String): Boolean {
        return chatService.deleteChat(chatId)
    }
}
