package com.example.cs501_project.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationService(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    suspend fun getCurrentLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // permissions not granted, handle this in your UI
            return null
        }

        // try getting the last known location first for a quick result
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
            _currentLocation.value = it
            return it
        }
        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let {
            _currentLocation.value = it
            return it
        }

        return _currentLocation.value // Return the last known (might be null)
    }
}