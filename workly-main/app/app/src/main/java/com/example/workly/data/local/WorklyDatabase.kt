package com.example.workly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.workly.data.local.dao.ServiceDao
import com.example.workly.data.local.entity.ServiceEntity

@Database(
    entities = [ServiceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WorklyDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
}
