package com.example.cs501_project.ui

import androidx.compose.material3.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
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

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (existingMarker == null) "Add Custom Marker" else "Update Custom Marker",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = markerTitle,
                    onValueChange = { markerTitle = it },
                    label = { Text("Title") }
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4)
                ) {
                    items(symbols) { symbol ->
                        Button(
                            onClick = {
                                markerSymbol = symbol
                            },
                            modifier = Modifier.padding(2.dp)
                        ) {
                            // drawable resource id for the image
                            val resId = remember(symbol) {
                                context.resources.getIdentifier(
                                    symbol,
                                    "drawable",
                                    context.packageName
                                )
                            }

                            // displaying marker symbol
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = "symbol",
                                modifier = Modifier.scale(scaleX = 2f, scaleY = 2f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End // align buttons to the end
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = { onConfirm(markerTitle, markerSymbol) },
                        enabled = markerTitle.isNotBlank() // enable only if title is entered
                    ) {
                        Text(
                            text = if (existingMarker == null) "Add" else "Update"
                        )
                    }
                }
            }
        }
    }
}