package com.example.cs501_project.ui

import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cs501_project.location.LocationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TravelEntryForm(
    context: Context,
    onSubmit: (String, Location?) -> Unit
) {
    var notes by remember { mutableStateOf("") }
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        currentLocation = LocationHelper.getCurrentLocation(context)
    }

    Column {
        currentLocation?.let {
            Text("Location: ${it.latitude}, ${it.longitude}", style = MaterialTheme.typography.bodyMedium)
        } ?: Text("Fetching location...", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Your Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                onSubmit(notes, currentLocation)
                notes = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}