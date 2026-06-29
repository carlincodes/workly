package com.example.workly.model

import com.google.gson.annotations.SerializedName

data class ServiceItem(
    val title: String,
    val description: String,
    val category: String,
    val buttonText: String,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("created_at")
    val createdAt: String = ""
)

data class ApiServiceResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class ApiServiceRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String
)

