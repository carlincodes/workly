package com.example.workly.repository

import com.example.workly.model.ServiceItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ServiceRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val servicesCollection = "services"

    suspend fun createService(service: ServiceItem): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val documentData = mapOf(
                "title" to service.title,
                "description" to service.description,
                "category" to service.category,
                "userId" to userId,
                "buttonText" to service.buttonText,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection(servicesCollection).document().set(documentData).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getServices(): List<ServiceItem> {
        return try {
            val userId = auth.currentUser?.uid ?: return emptyList()
            val snapshot = db.collection(servicesCollection)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                ServiceItem(
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    category = doc.getString("category") ?: "",
                    buttonText = doc.getString("buttonText") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAvailableServices(): List<ServiceItem> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: ""
            val snapshot = db.collection(servicesCollection)
                .whereNotEqualTo("userId", currentUserId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                ServiceItem(
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    category = doc.getString("category") ?: "",
                    buttonText = doc.getString("buttonText") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateService(
        serviceTitle: String,
        updatedService: ServiceItem
    ): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val query = db.collection(servicesCollection)
                .whereEqualTo("title", serviceTitle)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (query.documents.isNotEmpty()) {
                val doc = query.documents[0]
                db.collection(servicesCollection).document(doc.id).update(
                    mapOf(
                        "title" to updatedService.title,
                        "description" to updatedService.description,
                        "category" to updatedService.category,
                        "buttonText" to updatedService.buttonText
                    )
                ).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteService(serviceTitle: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val query = db.collection(servicesCollection)
                .whereEqualTo("title", serviceTitle)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (query.documents.isNotEmpty()) {
                val doc = query.documents[0]
                db.collection(servicesCollection).document(doc.id).delete().await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
