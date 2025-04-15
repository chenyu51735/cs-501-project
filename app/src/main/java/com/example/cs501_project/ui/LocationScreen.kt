package com.example.cs501_project.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cs501_project.viewmodel.LocationViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import coil.compose.AsyncImage

// location screen will display all location-related information and list nearby historical places
@Composable
fun LocationScreen(locationViewModel: LocationViewModel = viewModel()) {
    val context = LocalContext.current

    // state variables to track whether or not location permissions have been granted
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

    // observes state flows from the viewmodel and updates ui when a new location is received
    val historicalPlaces by locationViewModel.historicalPlaces.collectAsState()
    val currentLocation by locationViewModel.currentLocation.collectAsState()
    val currentCity by locationViewModel.currentCity.collectAsState()

    val customMarkers = remember { mutableStateListOf<Point>() } // going to hold all the marker points
    val historicalMarkerPoints = remember(historicalPlaces) { // these are the predefined suggestions markers
        historicalPlaces.map { Point.fromLngLat(it.geoSearchResult.lon, it.geoSearchResult.lat) }
    }

    // so the mapbox view knows where to zoom in
    val initialCameraPoint = remember(currentLocation) {
        currentLocation?.let { Point.fromLngLat(it.longitude, it.latitude) }
    }

    // used to launch the system's permission request dialogue
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // checking to see if we have fine location and coarse location permissions
            hasFineLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            hasCoarseLocationPermission = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        }
    )

    // if the permissions are not yet granted, will use LaunchedEffect to ask for them
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentLocation != null) {
            Text(
                text = "$currentCity",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))
            // displaying the mapbox and defining the parameters for it
            MapboxView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 16.dp),
                initialCameraPosition = initialCameraPoint,
                predefinedMarkerLocations = historicalMarkerPoints,
                onMapClick = { point ->
                    customMarkers.add(point)
                },
                customMarkers = customMarkers,
            )


            Text(
                text = "Explore these places next... \uD83D\uDDFA\uFE0F",  // added a map emoji
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
            if (historicalPlaces.isNotEmpty()) {
                LazyColumn {
                    items(historicalPlaces) { place ->
                        OutlinedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth()
                            ) {
                                if (!place.imageUrl.isNullOrEmpty()) {
                                    // if no image available, have a different format for the cards
                                    AsyncImage(
                                        model = place.imageUrl,
                                        contentDescription = place.geoSearchResult.title,
                                        modifier = Modifier.size(100.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = place.geoSearchResult.title,
                                    modifier = Modifier
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            } else {
                Text("No historical places found nearby 😞")
            }
        } else if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            Text("Location permissions not granted.")
        } else {
            Text("Getting location...") // maybe want to consider changing this to a loading animation
        }
    }
}