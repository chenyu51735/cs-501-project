package com.example.cs501_project.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object LocationHelper {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        // takes an instance of FusedLocationProviderClient
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        // suspend cancellable coroutine to bridge callback based lastLocation api
        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                // attempts to retrieve the last known location, and if successful,
                // resumes coroutine with location
                .addOnSuccessListener { location ->
                    cont.resume(location)
                }
                // if retrieving last location fails, resumes coroutine with null
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }
}
