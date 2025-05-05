package com.example.cs501_project.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cs501_project.ui.navigation.NavBar
import com.example.cs501_project.viewmodel.CustomMapMarker
import com.example.cs501_project.viewmodel.HistoricalPlaceWithImage
import com.example.cs501_project.viewmodel.LocationViewModel
import com.example.cs501_project.viewmodel.SettingsViewModel
import com.google.gson.Gson
import com.mapbox.geojson.Point

// location screen will display all location-related information and list nearby historical places
@Composable
fun LocationScreen(
    locationViewModel: LocationViewModel = viewModel(),
    onNavigateToFacts: (HistoricalPlaceWithImage) -> Unit,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController
) {
    val gson = Gson()
    val context = LocalContext.current


    val fontSize = settingsViewModel.fontSize.collectAsState().value
    // state variables to track whether or not location permissions have been granted
    var hasFineLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasCoarseLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasBackgroundLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    // permission for posting notification
    var hasNotificationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // observes state flows from the viewmodel and updates ui when a new location is received
    val historicalPlaces by locationViewModel.historicalPlaces.collectAsState()
    val currentLocation by locationViewModel.currentLocation.collectAsState()
    val currentCity by locationViewModel.currentCity.collectAsState()
    val customMarkers by locationViewModel.customMarkers.collectAsState()
    val currentUsername = locationViewModel.currentUsername

    val historicalMarkerPoints =
        remember(historicalPlaces) { // these are the predefined suggestions markers
            derivedStateOf { // using derived state of to stabilize these values
                historicalPlaces.map {
                    Point.fromLngLat(
                        it.geoSearchResult.lon,
                        it.geoSearchResult.lat
                    )
                }
            }
        }

    // so the mapbox view knows where to zoom in
    val initialCameraPoint = remember(currentLocation) {
        derivedStateOf {
            currentLocation?.let { Point.fromLngLat(it.longitude, it.latitude) }
        }
    }

    // states needed for adding custom markers
    var isCustomMarkerDialogVisible by remember { mutableStateOf(false) }
    var clickedPointForNewMarker by remember { mutableStateOf<Point?>(null) }

    // used to launch the system's permission request dialogue
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // checking to see if we have fine location and coarse location permissions
            hasFineLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            hasCoarseLocationPermission =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            hasNotificationPermission = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
            hasBackgroundLocationPermission =
                permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] ?: false
        }
    )

    // if the permissions are not yet granted, will use LaunchedEffect to ask for them
    LaunchedEffect(Unit) {
        if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val updatedMarkerResult: State<String?> = remember {
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow(
            "updatedCustomMarker",
            null
        )
    }?.collectAsState(null) ?: remember { mutableStateOf(null) }

    LaunchedEffect(updatedMarkerResult.value) {
        updatedMarkerResult.value?.let { encodedUpdatedMarker ->
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("updatedCustomMarker")
            val updatedMarker =
                gson.fromJson(Uri.decode(encodedUpdatedMarker), CustomMapMarker::class.java)
            locationViewModel.updateCustomMarker(updatedMarker)
        }
    }
    // Navbar
    Scaffold(
        bottomBar = { NavBar(navController) }
    ) { innerPadding ->
        // configuration of multiple device
        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp

        // displaying the historical places
        if (currentLocation != null) {
            if (screenWidthDp < 600) {
                // Phone view
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    item {
                        Text(
                            text = "Welcome to $currentCity, $currentUsername!",
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        MapboxView(
                            initialCameraPosition = initialCameraPoint.value,
                            predefinedMarkerLocations = historicalMarkerPoints.value,
                            onMapClick = { point ->
                                clickedPointForNewMarker = point
                                isCustomMarkerDialogVisible = true
                            },
                            customMarkers = customMarkers,
                            onNewCustomMarkerAdded = { point, title, symbol ->
                                locationViewModel.addCustomMarker(point, title, symbol)
                                isCustomMarkerDialogVisible = false
                                clickedPointForNewMarker = null
                            }
                        )
                    }

                    if (customMarkers.isNotEmpty()) {
                        items(customMarkers) { marker ->
                            CustomLocationCard(marker) {
                                navController.navigate(
                                    "customCardDetails/${
                                        Uri.encode(
                                            gson.toJson(
                                                marker
                                            )
                                        )
                                    }"
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            "Explore these places next... \uD83D\uDDFA\uFE0F",
                            modifier = Modifier.padding(16.dp),
                            fontSize = fontSize.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (historicalPlaces.isNotEmpty()) {
                        items(historicalPlaces) { place ->
                            OutlinedCard(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .clickable { onNavigateToFacts(place) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    if (!place.imageUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = place.imageUrl,
                                            contentDescription = place.geoSearchResult.title,
                                            modifier = Modifier.size(100.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Column {
                                        val placeLoc = locationViewModel.getStreetAddressFromCoordinates(place.geoSearchResult.lat, place.geoSearchResult.lon)
                                        Text(place.geoSearchResult.title, fontSize = fontSize.sp)
                                        Text(
                                            "Address: $placeLoc",
                                            fontSize = fontSize.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        item { Text("No historical places found nearby 😞") }
                    }
                }
            } else {
                // Tablet view
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Map on the left
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MapboxView(
                            initialCameraPosition = initialCameraPoint.value,
                            predefinedMarkerLocations = historicalMarkerPoints.value,
                            onMapClick = { point ->
                                clickedPointForNewMarker = point
                                isCustomMarkerDialogVisible = true
                            },
                            customMarkers = customMarkers,
                            onNewCustomMarkerAdded = { point, title, symbol ->
                                locationViewModel.addCustomMarker(point, title, symbol)
                                isCustomMarkerDialogVisible = false
                                clickedPointForNewMarker = null
                            }
                        )
                    }

                    // List on the right
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        item {
                            Text(
                                "Welcome to $currentCity, $currentUsername!",
                                fontSize = fontSize.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (customMarkers.isNotEmpty()) {
                            items(customMarkers) { marker ->
                                CustomLocationCard(marker) {
                                    navController.navigate(
                                        "customCardDetails/${
                                            Uri.encode(
                                                gson.toJson(
                                                    marker
                                                )
                                            )
                                        }"
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                "Explore these places next... \uD83D\uDDFA\uFE0F",
                                fontSize = fontSize.sp
                            )
                        }

                        if (historicalPlaces.isNotEmpty()) {
                            items(historicalPlaces) { place ->
                                OutlinedCard(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, Color.Black),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .clickable { onNavigateToFacts(place) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        if (!place.imageUrl.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = place.imageUrl,
                                                contentDescription = place.geoSearchResult.title,
                                                modifier = Modifier.size(100.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Column {
                                            val placeLoc = locationViewModel.getStreetAddressFromCoordinates(place.geoSearchResult.lat, place.geoSearchResult.lon)
                                            Text(
                                                place.geoSearchResult.title,
                                                fontSize = fontSize.sp
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                "Address: $placeLoc",
                                                fontSize = fontSize.sp
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            item { Text("No historical places found nearby 😞") }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
                    item { Text("Location permissions not granted.") }
                } else {
                    item { FlyingPlaneAnimation() }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item {
                        Text(
                            "Getting your location...",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Animation when getting location
@Composable
fun FlyingPlaneAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .offset(x = offsetX.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Send,
            contentDescription = "Flying Plane",
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )
    }
}
