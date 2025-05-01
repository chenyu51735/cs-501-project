package com.example.cs501_project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cs501_project.viewmodel.CustomMapMarker

@Composable
fun CustomMarkerDialog(
    onDismissRequest: () -> Unit, // when user clicks cancel or when they click out of the dialog
    onConfirm: (String, String) -> Unit, // callback to handle the user's input of title and symbol
    existingMarker: CustomMapMarker? = null // so that it can update an existing marker, optional
) {
    // marker title
    var markerTitle by remember { mutableStateOf("") }
    // symbol will default to default marker if not selected
    var markerSymbol by remember { mutableStateOf("default_marker.png") }
    val context = LocalContext.current.applicationContext

    // all the preset symbols for map overlay
    val symbols = listOf("beach", "boat", "coffee", "flower", "food",
        "movie", "museum", "park", "plane", "statue", "ticket", "tree")
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 375.dp, max = 500.dp)
                .padding(16.dp)
        ) {
            if (screenWidthDp < 600) {
                // Phone layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (existingMarker == null) "Add Custom Marker" else "Update Custom Marker",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = markerTitle,
                        onValueChange = { markerTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        items(symbols) { symbol ->
                            val resId = remember(symbol) {
                                context.resources.getIdentifier(symbol, "drawable", context.packageName)
                            }
                            Button(
                                onClick = { markerSymbol = symbol },
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = "symbol",
                                    modifier = Modifier.scale(2f)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = { onConfirm(markerTitle, markerSymbol) },
                            enabled = markerTitle.isNotBlank()
                        ) {
                            Text(if (existingMarker == null) "Add" else "Update")
                        }
                    }
                }
            } else {
                // Tablet layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left side: Form
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (existingMarker == null) "Add Custom Marker" else "Update Custom Marker",
                            style = MaterialTheme.typography.titleLarge
                        )

                        OutlinedTextField(
                            value = markerTitle,
                            onValueChange = { markerTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = onDismissRequest,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Cancel")
                            }
                            TextButton(
                                onClick = { onConfirm(markerTitle, markerSymbol) },
                                enabled = markerTitle.isNotBlank()
                            ) {
                                Text(if (existingMarker == null) "Add" else "Update")
                            }
                        }
                    }

                    // Right side: Symbol selector grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        items(symbols) { symbol ->
                            val resId = remember(symbol) {
                                context.resources.getIdentifier(symbol, "drawable", context.packageName)
                            }

                            Button(
                                onClick = { markerSymbol = symbol },
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp) // force same visible size
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = "symbol",
                                        modifier = Modifier.fillMaxSize(), // scale image to fit box
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}