package com.example.workly

import android.app.Application
import com.example.workly.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WorklyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WorklyApplication)
            modules(appModule)
        }
    }
}