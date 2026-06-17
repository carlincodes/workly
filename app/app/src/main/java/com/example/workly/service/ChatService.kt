package com.example.workly.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.workly.model.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

   
                                       
                                                             
   
class ChatService(private val firestore: FirebaseFirestore) {

       
                                 
       
    suspend fun sendMessage(
        chatId: String,
        message: ChatMessage
    ): Boolean = try {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.id)
            .set(message)
            .await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

       
                                      
       
    fun observeMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ChatMessage::class.java)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(messages)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

       
                                                
       
    suspend fun getOrCreateChat(userId1: String, userId2: String): String = try {
        val chatId = generateChatId(userId1, userId2)
        val docRef = firestore.collection("chats").document(chatId)
        
        docRef.get().await().let { doc ->
            if (!doc.exists()) {
                                  
                docRef.set(
                    mapOf(
                        "participants" to listOf(userId1, userId2),
                        "createdAt" to System.currentTimeMillis(),
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()
            }
        }
        chatId
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }

       
                                          
       
    fun getUserChats(userId: String): Flow<List<ChatConversation>> = callbackFlow {
        val listener = firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val chats = snapshot.documents.mapNotNull { doc ->
                        try {
                            ChatConversation(
                                chatId = doc.id,
                                participants = doc.get("participants") as? List<String> ?: emptyList(),
                                lastMessage = doc.getString("lastMessage") ?: "",
                                updatedAt = doc.getLong("updatedAt") ?: 0L
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(chats)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

       
                                         
       
    suspend fun updateLastMessage(
        chatId: String,
        lastMessage: String
    ): Boolean = try {
        firestore.collection("chats")
            .document(chatId)
            .update(
                mapOf(
                    "lastMessage" to lastMessage,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

       
                     
       
    suspend fun deleteChat(chatId: String): Boolean = try {
        firestore.collection("chats")
            .document(chatId)
            .delete()
            .await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    private fun generateChatId(userId1: String, userId2: String): String {
        val ids = listOf(userId1, userId2).sorted()
        return "${ids[0]}_${ids[1]}"
    }
}

data class ChatConversation(
    val chatId: String,
    val participants: List<String>,
    val lastMessage: String,
    val updatedAt: Long
)
