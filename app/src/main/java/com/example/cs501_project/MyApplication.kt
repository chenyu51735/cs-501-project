package com.example.cs501_project

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.*
import com.example.cs501_project.data.database.AppDatabase
import com.example.cs501_project.data.database.HistoricalPlaceRepository
import com.example.cs501_project.ui.notification.LocationBasedFactWorker

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        HistoricalPlaceRepository.initialize(applicationContext)
        scheduleLocationBasedFactWorker()
        AppDatabase.getDatabase(this)
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            "historical_facts_channel",
            "Fact Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel) // sets up the channel
    }

    private fun scheduleLocationBasedFactWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<LocationBasedFactWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(oneTimeWorkRequest)
    }
}