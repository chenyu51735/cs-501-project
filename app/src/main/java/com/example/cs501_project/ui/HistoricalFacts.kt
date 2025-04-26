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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cs501_project.ui.navigation.NavBar
import com.example.cs501_project.viewmodel.HistoricalPlaceWithImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalFacts(place: HistoricalPlaceWithImage,
                    navController: NavHostController
) {
    // Navbar
    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { Text(text = place.geoSearchResult.title) }
            )
        },
        bottomBar = { NavBar(navController) }
    ) { innerPadding ->
        // will take in a place and display its historical information
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(innerPadding)
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
    }
}
