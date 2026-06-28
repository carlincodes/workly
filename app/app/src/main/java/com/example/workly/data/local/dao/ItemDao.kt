package com.example.workly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.workly.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun observeFavorites(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE source = :source ORDER BY updatedAt DESC")
    suspend fun getItemsBySource(source: String): List<ItemEntity>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ItemEntity?

    @Query("SELECT id FROM items WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ItemEntity>)

    @Query("DELETE FROM items WHERE source = :source AND isFavorite = 0")
    suspend fun clearSourceCache(source: String)

    @Query("UPDATE items SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean, updatedAt: Long)

    @Query("DELETE FROM items WHERE id = :id AND isFavorite = 0")
    suspend fun deleteIfNotFavorite(id: String)
}