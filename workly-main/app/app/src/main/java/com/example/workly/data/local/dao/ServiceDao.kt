package com.example.workly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.workly.data.local.entity.ServiceEntity

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: ServiceEntity)

    @Update
    suspend fun update(service: ServiceEntity)

    @Query("SELECT * FROM services WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getByUserId(userId: String): List<ServiceEntity>

    @Query("SELECT * FROM services WHERE userId != :userId ORDER BY createdAt DESC")
    suspend fun getAvailableServices(userId: String): List<ServiceEntity>

    @Query("SELECT * FROM services WHERE title = :title AND userId = :userId LIMIT 1")
    suspend fun getByTitleAndUserId(title: String, userId: String): ServiceEntity?

    @Query("DELETE FROM services WHERE title = :title AND userId = :userId")
    suspend fun deleteByTitleAndUserId(title: String, userId: String)
}
