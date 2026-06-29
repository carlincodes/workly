package com.example.workly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.workly.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE isFavorite = 1 ORDER BY cachedAt DESC")
    fun getAllFavorites(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE source = :source ORDER BY cachedAt DESC")
    suspend fun getBySource(source: String): List<ItemEntity>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ItemEntity?

    @Query("SELECT id FROM items WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>)

    @Query("UPDATE items SET isFavorite = :isFavorite, cachedAt = :cachedAt WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean, cachedAt: Long)

    @Query("DELETE FROM items WHERE source = :source AND isFavorite = 0")
    suspend fun clearSourceCache(source: String)

    @Query("DELETE FROM items WHERE id = :id AND isFavorite = 0")
    suspend fun deleteIfNotFavorite(id: String)
}