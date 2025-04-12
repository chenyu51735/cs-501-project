package com.example.cs501_project.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cs501_project.viewmodel.LocationViewModel

@Composable
fun LocationScreen(locationViewModel: LocationViewModel = viewModel()) {
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

    val historicalPlaces by locationViewModel.historicalPlaces.collectAsState()
    val currentLocation by locationViewModel.currentLocation.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // checking to see if we have fine location and coarse location permissions
            hasFineLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            hasCoarseLocationPermission = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        }
    )

    LaunchedEffect(Unit) {
        if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // displaying the historical places
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentLocation != null) {
            Text("Your Current Location:")
            Text("Latitude: ${currentLocation?.latitude}, Longitude: ${currentLocation?.longitude}")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Nearby Historical Places:")
            if (historicalPlaces.isNotEmpty()) {
                LazyColumn {
                    items(historicalPlaces) { fact ->
                        Text(fact.title)
                    }
                }
            } else {
                Text("No historical places found nearby.")
            }
        } else if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            Text("Location permissions not granted.")
        } else {
            Text("Getting location...")
        }
    }
}