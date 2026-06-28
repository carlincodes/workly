package com.example.workly.data.repository

import com.example.workly.WorklyApp
import com.example.workly.api.ApiService
import com.example.workly.api.RetrofitClient
import com.example.workly.data.local.dao.ItemDao
import com.example.workly.data.local.db.AppDatabase
import com.example.workly.data.local.entity.ItemEntity
import com.example.workly.data.local.entity.ItemSource
import com.example.workly.data.local.entity.stableId
import com.example.workly.model.ServiceItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ItemsRepositoryImpl(
    private val itemDao: ItemDao = AppDatabase.getInstance(WorklyApp.instance).itemDao(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val apiService: ApiService = RetrofitClient.apiService
) {
    private val servicesCollection = "services"

    fun getFavoriteItems(): Flow<List<ServiceItem>> {
        return itemDao.observeFavorites().map { items ->
            items.map(ItemEntity::toDomain)
        }
    }

    suspend fun setFavorite(item: ServiceItem, isFavorite: Boolean) {
        val itemId = item.stableId()
        val existing = itemDao.getById(itemId)

        if (existing == null) {
            itemDao.upsert(ItemEntity.fromDomain(item, ItemSource.LOCAL_ONLY, isFavorite = isFavorite))
            return
        }

        itemDao.updateFavorite(itemId, isFavorite, System.currentTimeMillis())

        if (!isFavorite && existing.source == ItemSource.LOCAL_ONLY.name) {
            itemDao.deleteIfNotFavorite(itemId)
        }
    }

    suspend fun createService(service: ServiceItem): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val document = firestore.collection(servicesCollection).document()
            val createdAt = System.currentTimeMillis().toString()
            val remoteService = service.copy(
                id = document.id,
                userId = userId,
                createdAt = createdAt
            )

            document.set(
                mapOf(
                    "id" to remoteService.id,
                    "title" to remoteService.title,
                    "description" to remoteService.description,
                    "category" to remoteService.category,
                    "userId" to userId,
                    "buttonText" to remoteService.buttonText,
                    "createdAt" to createdAt,
                    "timestamp" to System.currentTimeMillis()
                )
            ).await()

            itemDao.upsert(ItemEntity.fromDomain(remoteService, ItemSource.FIRESTORE_USER))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getServices(): List<ServiceItem> {
        return try {
            val userId = auth.currentUser?.uid ?: return emptyList()
            val snapshot = firestore.collection(servicesCollection)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull(::mapFirestoreDocument)
            cacheItems(items, ItemSource.FIRESTORE_USER)
            items
        } catch (e: Exception) {
            e.printStackTrace()
            getCachedItems(ItemSource.FIRESTORE_USER)
        }
    }

    suspend fun getAvailableServices(): List<ServiceItem> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: ""
            val snapshot = firestore.collection(servicesCollection)
                .whereNotEqualTo("userId", currentUserId)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull(::mapFirestoreDocument)
            cacheItems(items, ItemSource.FIRESTORE_AVAILABLE)
            items
        } catch (e: Exception) {
            e.printStackTrace()
            getCachedItems(ItemSource.FIRESTORE_AVAILABLE)
        }
    }

    suspend fun getServicesFromApi(): List<ServiceItem> {
        return try {
            val response = apiService.getServices(limit = 20)
            if (!response.isSuccessful) {
                return getCachedItems(ItemSource.API)
            }

            val items = response.body()?.map { apiResponse ->
                ServiceItem(
                    title = apiResponse.title,
                    description = apiResponse.description,
                    category = apiResponse.category,
                    buttonText = "Ver detalhes",
                    id = apiResponse.id,
                    userId = apiResponse.userId,
                    createdAt = apiResponse.createdAt
                )
            }.orEmpty()

            cacheItems(items, ItemSource.API)
            items
        } catch (e: Exception) {
            e.printStackTrace()
            getCachedItems(ItemSource.API)
        }
    }

    suspend fun updateService(serviceTitle: String, updatedService: ServiceItem): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val query = firestore.collection(servicesCollection)
                .whereEqualTo("title", serviceTitle)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val document = query.documents.firstOrNull() ?: return false
            val persistedService = updatedService.copy(
                id = document.getString("id") ?: document.id,
                userId = userId,
                createdAt = document.getString("createdAt") ?: updatedService.createdAt
            )

            firestore.collection(servicesCollection).document(document.id).update(
                mapOf(
                    "id" to persistedService.id,
                    "title" to persistedService.title,
                    "description" to persistedService.description,
                    "category" to persistedService.category,
                    "buttonText" to persistedService.buttonText,
                    "createdAt" to persistedService.createdAt,
                    "timestamp" to System.currentTimeMillis()
                )
            ).await()

            itemDao.upsert(ItemEntity.fromDomain(persistedService, ItemSource.FIRESTORE_USER))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteService(serviceTitle: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val query = firestore.collection(servicesCollection)
                .whereEqualTo("title", serviceTitle)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val document = query.documents.firstOrNull() ?: return false
            firestore.collection(servicesCollection).document(document.id).delete().await()
            itemDao.deleteIfNotFavorite(document.getString("id") ?: document.id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun cacheItems(items: List<ServiceItem>, source: ItemSource) {
        val favoriteIds = itemDao.getFavoriteIds().toSet()
        itemDao.clearSourceCache(source.name)
        itemDao.upsertAll(
            items.map { item ->
                ItemEntity.fromDomain(
                    item = item,
                    source = source,
                    isFavorite = item.stableId() in favoriteIds
                )
            }
        )
    }

    private suspend fun getCachedItems(source: ItemSource): List<ServiceItem> {
        return itemDao.getItemsBySource(source.name).map(ItemEntity::toDomain)
    }

    private fun mapFirestoreDocument(document: DocumentSnapshot): ServiceItem? {
        val title = document.getString("title") ?: return null
        return ServiceItem(
            title = title,
            description = document.getString("description").orEmpty(),
            category = document.getString("category").orEmpty(),
            buttonText = document.getString("buttonText") ?: "Ver detalhes",
            id = document.getString("id") ?: document.id,
            userId = document.getString("userId").orEmpty(),
            createdAt = document.getString("createdAt").orEmpty()
        )
    }
}