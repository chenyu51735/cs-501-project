package com.example.cs501_project.ui.notification

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.cs501_project.R
import com.example.cs501_project.viewmodel.LocationViewModel
import kotlin.random.Random

// notification handler defines how the user can handle the notification after they receive it
class NotificationHandler(
    private val context: Context,
    private val locationViewModel: LocationViewModel
) {
    // notification manager responsible for handling notifications
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "notification_channel_id" // unique identifier for channel

    fun showFactNotification() {
        val facts = locationViewModel.historicalPlaces.value[0].historicalFacts[0]
        val title = locationViewModel.historicalPlaces.value[0].geoSearchResult.title
        Log.d("Notification Handler", facts)
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle("Did you know this about $title?")
            .setContentText(facts)
            .setSmallIcon(R.drawable.round_notifications_24) // using the logo as the small icon
            .setPriority(NotificationManager.IMPORTANCE_HIGH) // sets priority as high for notification
            .setAutoCancel(true)
            .build() // finalizes the creation of notification

        notificationManager.notify(Random.nextInt(), notification) // triggers display of notification
        // Random.nextInt() generates a unique identifier for notification and treats each as its own instance
    }
}