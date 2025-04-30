package com.example.cs501_project

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.cs501_project.data.database.AppDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannel(
            "notification_channel_id",
            "Notification name",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel) // sets up the channel
        AppDatabase.getDatabase(this)
    }
}