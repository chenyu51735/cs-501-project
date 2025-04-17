package com.example.cs501_project.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cs501_project.viewmodel.HistoricalPlaceWithImage

@Composable
fun HistoricalFacts(place: HistoricalPlaceWithImage) {
    // will take in a place and display its historical information
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!place.imageUrl.isNullOrEmpty()) {
            // if no image available, have a different format for the cards
            AsyncImage(
                model = place.imageUrl,
                contentDescription = place.geoSearchResult.title,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        place.historicalFacts?.let {
            Text(
                text = it
            )
        }
    }
}