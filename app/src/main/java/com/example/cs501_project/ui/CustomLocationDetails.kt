package com.example.cs501_project.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// when a user clicks on the card of their own custom marked location, lead them to this screen
@Composable
fun CustomLocationDetails(marker: CustomMapMarker) {
    Text(text = marker.title)
}