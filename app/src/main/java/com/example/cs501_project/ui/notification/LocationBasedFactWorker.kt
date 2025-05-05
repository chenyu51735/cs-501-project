package com.example.cs501_project.ui.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cs501_project.data.database.HistoricalPlaceRepository
import com.example.cs501_project.location.LocationService
import kotlinx.coroutines.flow.collectLatest
import kotlin.random.Random

class LocationBasedFactWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    private val locationService = LocationService(context)
    private val notificationHandler = NotificationHandler(context)

    // gathers location to start doing work
    override suspend fun doWork(): Result {
        locationService.getLocationUpdates()
            .collectLatest { location ->
                if (location != null) {
                    triggerNotificationsForNearbyPlaces()
                }
            }
        return Result.success()
    }

    // selects a random place from the historical place repository based on location to show a fact
    private suspend fun triggerNotificationsForNearbyPlaces() {
        val historicalPlaces = HistoricalPlaceRepository.getHistoricalPlaces()
        Log.d("LocationBasedFactWorker", historicalPlaces.toString())
        historicalPlaces.collectLatest { place ->
            val max = place.size - 1
            val idx = Random.nextInt(0, max)
            notificationHandler.showFactNotification(place[idx])
        }

    }
}