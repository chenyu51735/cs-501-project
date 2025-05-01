package com.example.cs501_project.ui.notification

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.cs501_project.R
import com.example.cs501_project.model.HistoricalPlace
import kotlin.random.Random

// notification handler defines how the user can handle the notification after they receive it
class NotificationHandler(
    private val context: Context
) {
    // notification manager responsible for handling notifications
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelId = "historical_facts_channel" // unique identifier for channel

    fun showFactNotification(place: HistoricalPlace) {
        Log.d("Notification Handler", "showFactNotification() called")
        try {
            val title = place.title
            val fact = place.historicalFacts.toString()
            Log.d("Notification Handler", fact)
            val notification = NotificationCompat.Builder(context, notificationChannelId)
                .setContentTitle("Did you know this about $title?")
                .setContentText(fact)
                .setSmallIcon(R.drawable.round_notifications_24) // using the logo as the small icon
                .setPriority(NotificationManager.IMPORTANCE_HIGH) // sets priority as high for notification
                .setAutoCancel(true)
                .build() // finalizes the creation of notification

            notificationManager.notify(
                Random.nextInt(),
                notification
            ) // triggers display of notification
            // Random.nextInt() generates a unique identifier for notification and treats each as its own instance
        } catch (e: Exception) {
            Log.e("Notification Handler", "Error showing notification: ${e.message}", e)
        }
    }
}