package com.example.cs501_project.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.cs501_project.location.LocationService
import kotlinx.coroutines.launch

@Composable
fun LocationScreen(locationService: LocationService) {
    val context = LocalContext.current
    var hasFineLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasCoarseLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val scope = rememberCoroutineScope()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasFineLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            hasCoarseLocationPermission = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        }
    )

    LaunchedEffect(Unit) {
        if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        } else {
            // permissions are granted, get the location
            scope.launch {
                currentLocation = locationService.getCurrentLocation()
            }
        }
    }

    // display your UI based on the location data
    if (currentLocation != null) {
        Text("Latitude: ${currentLocation?.latitude}, Longitude: ${currentLocation?.longitude}")
    } else if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
        Text("Location permissions not granted.")
    } else {
        Text("Getting location...")
    }
}