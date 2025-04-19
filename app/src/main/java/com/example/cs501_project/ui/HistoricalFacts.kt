package com.example.cs501_project.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cs501_project.viewmodel.HistoricalPlaceWithImage

@Composable
fun HistoricalFacts(place: HistoricalPlaceWithImage) {
    // will take in a place and display its historical information
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
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

        LazyColumn {
            items(place.historicalFacts) { fact ->
                OutlinedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(text = fact)
                }
            }
        }
    }
    // maybe add a user input card, having a preset number of symbols?
}
