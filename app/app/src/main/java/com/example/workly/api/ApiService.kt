package com.example.workly.api

import com.example.workly.model.ApiServiceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("_limit") limit: Int = 10
    ): Response<List<ApiServiceResponse>>

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") userId: Int
    ): Response<ApiServiceResponse>

    @GET("posts")
    suspend fun getServices(
        @Query("_limit") limit: Int = 20,
        @Query("_sort") sort: String = "id",
        @Query("_order") order: String = "asc"
    ): Response<List<ApiServiceResponse>>

    @GET("posts/{id}")
    suspend fun getServiceById(
        @Path("id") serviceId: Int
    ): Response<ApiServiceResponse>
}
