package com.example.workly.data.local.mapper

import com.example.workly.data.local.entity.ServiceEntity
import com.example.workly.model.ServiceItem
import java.util.UUID

fun ServiceEntity.toServiceItem(): ServiceItem {
    return ServiceItem(
        id = id,
        title = title,
        description = description,
        category = category,
        buttonText = buttonText,
        userId = userId,
        createdAt = createdAt.toString()
    )
}

fun ServiceItem.toEntity(userId: String, createdAt: Long = System.currentTimeMillis()): ServiceEntity {
    return ServiceEntity(
        id = id.ifBlank { UUID.randomUUID().toString() },
        title = title,
        description = description,
        category = category,
        buttonText = buttonText,
        userId = userId,
        createdAt = createdAt
    )
}
