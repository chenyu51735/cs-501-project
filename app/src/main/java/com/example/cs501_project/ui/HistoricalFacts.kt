package com.example.cs501_project.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.cs501_project.viewmodel.HistoricalPlaceWithImage

@Composable
fun HistoricalFacts(place: HistoricalPlaceWithImage) {
    // will take in a place and display its historical information
    place.historicalFacts?.let { Text(text = it) }
}