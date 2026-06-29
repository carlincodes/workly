package com.example.workly.data.repository

import android.content.Context
import com.example.workly.data.local.dao.ItemDao
import com.example.workly.data.local.db.AppDatabase
import com.example.workly.data.local.entity.ItemSource
import com.example.workly.data.local.entity.toDomain
import com.example.workly.data.local.entity.toEntity
import com.example.workly.model.Service
import com.example.workly.network.CepResponse
import com.example.workly.network.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ItemsRepositoryImpl(
    context: Context,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val itemDao: ItemDao = AppDatabase.getInstance(context).itemDao()
) {
    private val servicesCollection = firestore.collection("services")

    // Local strategy:
    // - Favorites are persisted in Room (isFavorite = true)
    // - Firestore services are cached in Room for offline fallback
    // - Local history can be saved as LOCAL_HISTORY source

    fun getAllFavorites(): Flow<List<Service>> {
        return itemDao.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun setFavorite(service: Service, isFavorite: Boolean) {
        val entity = itemDao.getById(service.id.ifBlank { fallbackId(service) })

        if (entity == null) {
            itemDao.insert(service.toEntity(source = ItemSource.LOCAL_HISTORY, isFavorite = isFavorite))
            return
        }

        itemDao.updateFavorite(entity.id, isFavorite, System.currentTimeMillis())
        if (!isFavorite && entity.source == ItemSource.LOCAL_HISTORY.name) {
            itemDao.deleteIfNotFavorite(entity.id)
        }
    }

    suspend fun createService(service: Service): Boolean {
        return try {
            val docRef = servicesCollection.document()
            val serviceToSave = service.copy(id = docRef.id)
            docRef.set(serviceToSave).await()
            itemDao.insert(serviceToSave.toEntity(source = ItemSource.FIRESTORE))
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getServices(): List<Service> {
        return try {
            val snapshot = servicesCollection.get().await()
            val services = snapshot.toObjects(Service::class.java)
            cacheFirestoreServices(services)
            services
        } catch (_: Exception) {
            itemDao.getBySource(ItemSource.FIRESTORE.name).map { it.toDomain() }
        }
    }

    suspend fun getServiceById(serviceId: String): Service? {
        return try {
            servicesCollection.document(serviceId).get().await().toObject(Service::class.java)
        } catch (_: Exception) {
            itemDao.getById(serviceId)?.toDomain()
        }
    }

    suspend fun updateService(service: Service): Boolean {
        return try {
            if (service.id.isBlank()) return false
            servicesCollection.document(service.id).set(service).await()

            val isFavorite = itemDao.getFavoriteIds().contains(service.id)
            itemDao.insert(service.toEntity(source = ItemSource.FIRESTORE, isFavorite = isFavorite))
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun deleteService(serviceId: String): Boolean {
        return try {
            servicesCollection.document(serviceId).delete().await()
            itemDao.deleteIfNotFavorite(serviceId)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getAddressByCep(cep: String): CepResponse {
        return RetrofitClient.viaCepService.getAddress(cep)
    }

    private suspend fun cacheFirestoreServices(services: List<Service>) {
        val favoriteIds = itemDao.getFavoriteIds().toSet()
        itemDao.clearSourceCache(ItemSource.FIRESTORE.name)
        itemDao.insertAll(
            services.map { service ->
                val id = service.id.ifBlank { fallbackId(service) }
                service.copy(id = id).toEntity(
                    source = ItemSource.FIRESTORE,
                    isFavorite = favoriteIds.contains(id)
                )
            }
        )
    }

    private fun fallbackId(service: Service): String {
        return listOf(
            service.title,
            service.description,
            service.jobType,
            service.clientId,
            service.status
        ).joinToString("|")
    }
}