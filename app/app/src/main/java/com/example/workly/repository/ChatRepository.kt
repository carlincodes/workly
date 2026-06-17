package com.example.workly.repository

import com.example.workly.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val chatCollection = db.collection("chat_messages")

    fun getChatMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val listener = chatCollection
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents
                    ?.mapNotNull { doc ->
                        ChatMessage(
                            id = doc.id,
                            senderId = doc.getString("senderId") ?: "",
                            senderName = doc.getString("senderName") ?: "Anônimo",
                            text = doc.getString("text") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    } ?: emptyList()
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(text: String, senderName: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val message = mapOf(
                "senderId" to userId,
                "senderName" to senderName.ifBlank { "Usuário" },
                "text" to text,
                "timestamp" to System.currentTimeMillis()
            )
            chatCollection.add(message).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
