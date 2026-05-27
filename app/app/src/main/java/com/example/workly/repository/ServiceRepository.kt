package com.example.workly.repository

import android.util.Log
import com.example.workly.model.Service
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ServiceRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val servicesCollection = firestore.collection("services")

    suspend fun createService(service: Service): Boolean {
        return try {
            servicesCollection.add(service).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreError", "Erro ao criar serviço", e)
            false
        }
    }

    suspend fun getServices(): List<Service> {
        return try {
            val result = servicesCollection.get().await()
            Log.d("FirestoreData", "Documentos encontrados: ${result.size()}")
            result.toObjects(Service::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Erro ao buscar serviços", e)
            emptyList()
        }
    }

    suspend fun updateService(service: Service) {
        if (service.id.isNotEmpty()) {
            servicesCollection.document(service.id).set(service).await()
        }
    }

    suspend fun deleteService(serviceId: String) {
        servicesCollection.document(serviceId).delete().await()
    }
}
