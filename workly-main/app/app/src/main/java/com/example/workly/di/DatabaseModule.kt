package com.example.workly.di

import androidx.room.Room
import com.example.workly.data.local.WorklyDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            WorklyDatabase::class.java,
            "workly_database"
        ).build()
    }

    single { get<WorklyDatabase>().serviceDao() }
}
