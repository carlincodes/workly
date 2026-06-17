package com.example.workly.repository

import com.example.workly.api.RetrofitClient
import com.example.workly.model.ApiServiceResponse
import com.example.workly.model.ServiceItem

class ApiRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getServicesFromApi(): List<ServiceItem> {
        return try {
            val response = apiService.getServices(limit = 20)
            if (response.isSuccessful) {
                response.body()?.map { apiResponse ->
                    ServiceItem(
                        title = apiResponse.title,
                        description = apiResponse.description,
                        category = apiResponse.category,
                        buttonText = "Ver detalhes",
                        id = apiResponse.id,
                        userId = apiResponse.userId,
                        createdAt = apiResponse.createdAt
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUsersFromApi(): List<ServiceItem> {
        return try {
            val response = apiService.getUsers(limit = 10)
            if (response.isSuccessful) {
                response.body()?.map { apiResponse ->
                    ServiceItem(
                        title = apiResponse.title,
                        description = apiResponse.description,
                        category = "Usuário",
                        buttonText = "Conectar",
                        id = apiResponse.id,
                        userId = apiResponse.userId,
                        createdAt = apiResponse.createdAt
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getServiceByIdFromApi(serviceId: Int): ServiceItem? {
        return try {
            val response = apiService.getServiceById(serviceId)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    ServiceItem(
                        title = apiResponse.title,
                        description = apiResponse.description,
                        category = apiResponse.category,
                        buttonText = "Ver detalhes",
                        id = apiResponse.id,
                        userId = apiResponse.userId,
                        createdAt = apiResponse.createdAt
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
