package com.example.workly.repository

import com.example.workly.model.ProfileData
import com.example.workly.model.ProviderLocationInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val profileCollection = db.collection("profiles")
    private val providersCollection = db.collection("providers")

    suspend fun fetchProfileData(): ProfileData? {
        val userId = auth.currentUser?.uid ?: return null
        val snapshot = profileCollection.document(userId).get().await()
        return if (snapshot.exists()) {
            ProfileData(
                name = snapshot.getString("name") ?: "",
                email = snapshot.getString("email") ?: auth.currentUser?.email.orEmpty(),
                phone = snapshot.getString("phone") ?: "",
                profession = snapshot.getString("profession") ?: "",
                description = snapshot.getString("description") ?: ""
            )
        } else {
            null
        }
    }

    suspend fun saveProfileData(profileData: ProfileData): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val payload = mapOf(
            "name" to profileData.name,
            "email" to profileData.email,
            "phone" to profileData.phone,
            "profession" to profileData.profession,
            "description" to profileData.description
        )
        return try {
            profileCollection.document(userId).set(payload).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

       
                                                                                 
                                                  
       
    suspend fun updateProviderLocation(
        providerId: String,
        latitude: Double,
        longitude: Double
    ): Boolean = try {
        providersCollection.document(providerId).update(
            mapOf(
                "latitude" to latitude,
                "longitude" to longitude,
                "lastLocationUpdate" to System.currentTimeMillis()
            )
        ).await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

       
                                                   
       
    suspend fun getProviderById(providerId: String): ProviderLocationInfo? {
        return try {
            val snapshot = providersCollection.document(providerId).get().await()
            if (snapshot.exists()) {
                ProviderLocationInfo(
                    providerId = snapshot.id,
                    name = snapshot.getString("name") ?: "",
                    specialty = snapshot.getString("specialty") ?: "",
                    rating = snapshot.getDouble("rating")?.toFloat() ?: 0f,
                    profileImageUrl = snapshot.getString("profileImageUrl") ?: "",
                    latitude = snapshot.getDouble("latitude") ?: 0.0,
                    longitude = snapshot.getDouble("longitude") ?: 0.0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

       
                                                       
       
    suspend fun getAllProvidersWithLocation(): List<ProviderLocationInfo> {
        return try {
            val snapshot = providersCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    ProviderLocationInfo(
                        providerId = doc.id,
                        name = doc.getString("name") ?: "",
                        specialty = doc.getString("specialty") ?: "",
                        rating = doc.getDouble("rating")?.toFloat() ?: 0f,
                        profileImageUrl = doc.getString("profileImageUrl") ?: "",
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
