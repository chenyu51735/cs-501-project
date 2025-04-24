package com.example.cs501_project

import android.app.Application
import com.example.cs501_project.data.database.AppDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this)
    }
}