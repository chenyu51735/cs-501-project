package com.example.cs501_project.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cs501_project.ui.navigation.NavBar
import com.example.cs501_project.viewmodel.HistoricalPlaceWithImage
import com.example.cs501_project.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalFacts(place: HistoricalPlaceWithImage, navController: NavHostController, settingsViewModel: SettingsViewModel
) {
    val fontSize = settingsViewModel.fontSize.collectAsState().value
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

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
                title = { Text(text = place.geoSearchResult.title, fontSize = fontSize.sp) }
            )
        },
        bottomBar = { NavBar(navController) }
    ) { innerPadding ->
        // will take in a place and display its historical information
        if (screenWidthDp < 600) {
            // 📱 PHONE VIEW: vertical layout
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    if (!place.imageUrl.isNullOrEmpty()) {
                        // if no image available, have a different format for the cards
                        AsyncImage(
                            model = place.imageUrl,
                            contentDescription = place.geoSearchResult.title,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .size(220.dp)
                        )
                    }
                }
                item {
                    Text(
                        text = "Historical Facts",
                        fontSize = (fontSize + 2).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                // display all facts as individual cards
                items(place.historicalFacts) { fact ->
                    OutlinedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = fact,
                            fontSize = fontSize.sp,
                            modifier = Modifier.padding(16.dp),
                            lineHeight = (fontSize + 6).sp
                        )
                    }
                }
            }
        } else {
            // Tablet view
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left side with images
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!place.imageUrl.isNullOrEmpty()) {
                        // if no image available, have a different format for the cards
                        AsyncImage(
                            model = place.imageUrl,
                            contentDescription = place.geoSearchResult.title,
                            modifier = Modifier
                                .size(500.dp)
                                .padding(16.dp)
                        )
                    }
                }

                // Right side with facts
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Historical Facts",
                        fontSize = (fontSize + 4).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // render facts as cards
                    place.historicalFacts.forEach { fact ->
                        OutlinedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(1.dp, Color.Gray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = fact,
                                fontSize = fontSize.sp,
                                modifier = Modifier.padding(16.dp),
                                lineHeight = (fontSize + 6).sp
                            )
                        }
                    }
                }
            }
        }
    }
}