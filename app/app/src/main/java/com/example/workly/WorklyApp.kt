package com.example.workly

import android.app.Application

class WorklyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: WorklyApp
            private set
    }
}