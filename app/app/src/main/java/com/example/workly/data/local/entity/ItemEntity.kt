package com.example.workly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.workly.model.Service

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val jobType: String,
    val clientId: String,
    val status: String,
    val source: String,
    val isFavorite: Boolean,
    val cachedAt: Long
)

enum class ItemSource {
    FIRESTORE,
    LOCAL_HISTORY
}

fun ItemEntity.toDomain(): Service {
    return Service(
        id = id,
        title = title,
        description = description,
        jobType = jobType,
        clientId = clientId,
        status = status
    )
}

fun Service.toEntity(
    source: ItemSource = ItemSource.FIRESTORE,
    isFavorite: Boolean = false,
    cachedAt: Long = System.currentTimeMillis()
): ItemEntity {
    return ItemEntity(
        id = if (id.isBlank()) fallbackId() else id,
        title = title,
        description = description,
        jobType = jobType,
        clientId = clientId,
        status = status,
        source = source.name,
        isFavorite = isFavorite,
        cachedAt = cachedAt
    )
}

private fun Service.fallbackId(): String {
    return listOf(title, description, jobType, clientId, status)
        .joinToString("|")
}