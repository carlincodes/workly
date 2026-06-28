package com.example.workly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.workly.model.ServiceItem

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val buttonText: String,
    val userId: String,
    val createdAt: String,
    val source: String,
    val isFavorite: Boolean,
    val updatedAt: Long
) {
    fun toDomain(): ServiceItem {
        return ServiceItem(
            title = title,
            description = description,
            category = category,
            buttonText = buttonText,
            id = id,
            userId = userId,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(
            item: ServiceItem,
            source: ItemSource,
            isFavorite: Boolean = false,
            updatedAt: Long = System.currentTimeMillis()
        ): ItemEntity {
            return ItemEntity(
                id = item.stableId(),
                title = item.title,
                description = item.description,
                category = item.category,
                buttonText = item.buttonText,
                userId = item.userId,
                createdAt = item.createdAt,
                source = source.name,
                isFavorite = isFavorite,
                updatedAt = updatedAt
            )
        }
    }
}

enum class ItemSource {
    FIRESTORE_USER,
    FIRESTORE_AVAILABLE,
    API,
    LOCAL_ONLY
}

fun ServiceItem.stableId(): String {
    return id.ifBlank {
        listOf(title, description, category, userId, createdAt)
            .joinToString(separator = "|")
    }
}