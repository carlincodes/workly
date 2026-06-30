package com.example.workly.repository

import com.example.workly.data.local.dao.ServiceDao
import com.example.workly.data.local.mapper.toEntity
import com.example.workly.data.local.mapper.toServiceItem
import com.example.workly.model.ServiceItem
import com.google.firebase.auth.FirebaseAuth

class ServiceRepository(
    private val serviceDao: ServiceDao,
    private val auth: FirebaseAuth
) {

    suspend fun createService(service: ServiceItem): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            serviceDao.insert(service.toEntity(userId))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getServices(): List<ServiceItem> {
        return try {
            val userId = auth.currentUser?.uid ?: return emptyList()
            serviceDao.getByUserId(userId).map { it.toServiceItem() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAvailableServices(): List<ServiceItem> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return emptyList()
            serviceDao.getAvailableServices(currentUserId).map { it.toServiceItem() }
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
            val existing = serviceDao.getByTitleAndUserId(serviceTitle, userId) ?: return false

            serviceDao.update(
                existing.copy(
                    title = updatedService.title,
                    description = updatedService.description,
                    category = updatedService.category,
                    buttonText = updatedService.buttonText
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteService(serviceTitle: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            serviceDao.deleteByTitleAndUserId(serviceTitle, userId)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
