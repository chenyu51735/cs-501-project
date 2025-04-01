package com.example.cs501_project.ui

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cs501_project.viewmodel.TravelViewModel
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TravelApp(viewModel: TravelViewModel = viewModel()) {
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val entries by viewModel.entries.collectAsState()

    LaunchedEffect(Unit) {
        locationPermission.launchPermissionRequest()
    }

    if (locationPermission.status.isGranted) {
        Column(modifier = Modifier.padding(16.dp)) {
            TravelEntryForm(context = context) { notes, location ->
                viewModel.addEntry(notes, location)
            }
            Spacer(Modifier.height(16.dp))
            TravelLog(entries = entries)
        }
    } else {
        Text("Location permission is required to fetch your location.")
    }
}

