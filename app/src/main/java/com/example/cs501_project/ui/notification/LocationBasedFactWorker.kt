package com.example.cs501_project.ui.notification

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cs501_project.data.database.HistoricalPlaceRepository
import com.example.cs501_project.location.LocationService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.forEach

class LocationBasedFactWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    private val locationService = LocationService(context)
    private val notificationHandler = NotificationHandler(context)

    override suspend fun doWork(): Result {
        locationService.getLocationUpdates()
            .collectLatest { location ->
                if (location != null) {
                    triggerNotificationsForNearbyPlaces()
                }
            }
        return Result.success()
    }

    private suspend fun triggerNotificationsForNearbyPlaces() {
        val historicalPlaces = HistoricalPlaceRepository.getHistoricalPlaces()
        historicalPlaces.collectLatest { place ->
            notificationHandler.showFactNotification(place[0])
            Log.d("LocationBasedFactWorker", place[0].historicalFacts!!.toString())
        }

    }
}