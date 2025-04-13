package com.example.cs501_project.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

// encapsulates all the logic needed for accessing device's location
// and provides it as a Flow of updates so it processes as the info comes
class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // mutable stateflow that holds last retrieved location
    private val _currentLocation = MutableStateFlow<Location?>(null)
    // immutable stateflow that is exposed to other parts of the app to observe the latest location
    val currentLocation: StateFlow<Location?> = _currentLocation

    @SuppressLint("MissingPermission")
    // returns a flow that emits location updates at the specific interval
    fun getLocationUpdates(intervalMillis: Long = 10000): Flow<Location?> = callbackFlow {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            trySend(null)
            close()
            return@callbackFlow
            // callback Flow converts callback-based location updates from FPLC to Flow
        }

        // configures the parameters for requesting location updates
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(10f)
            .setMaxUpdateDelayMillis(intervalMillis * 2)
            .build()

        // an object that receives location updates from the FPLC
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    _currentLocation.value = location
                    trySend(location).isSuccess
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        // will ensure that location updates are stopped when the Flow is no longer being collected
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}