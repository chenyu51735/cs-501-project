package com.example.cs501_project

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.*
import com.example.cs501_project.data.database.AppDatabase
import com.example.cs501_project.data.database.HistoricalPlaceRepository
import com.example.cs501_project.ui.notification.LocationBasedFactWorker
import java.util.concurrent.TimeUnit

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
        Log.d("MyApplication", "scheduleLocationBasedFactWorker() CALLED")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<LocationBasedFactWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "locationBasedFactWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }
}