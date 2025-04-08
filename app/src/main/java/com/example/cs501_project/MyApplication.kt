package com.example.cs501_project

import android.app.Application
import com.example.cs501_project.data.database.DatabaseManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseManager.getDatabase(this)
    }
}