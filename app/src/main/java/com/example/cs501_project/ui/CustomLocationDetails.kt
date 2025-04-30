package com.example.cs501_project.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs501_project.viewmodel.CustomMapMarker
import com.example.cs501_project.viewmodel.SettingsViewModel

// when a user clicks on the card of their own custom marked location, lead them to this screen
@Composable
fun CustomLocationDetails(
    marker: CustomMapMarker,
    onMarkerUpdated: (CustomMapMarker) -> Unit, // callback to send marker back
    settingsViewModel: SettingsViewModel
) {
    val fontSize = settingsViewModel.fontSize.collectAsState().value
    var isUpdateDialogVisible by remember { mutableStateOf(false) }
    var userNotes by rememberSaveable { mutableStateOf(marker.notes) }

    Column {
        Text(
            text = marker.title,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
        // update CustomMarkerDialog to take in an existing marker in order to reuse it to update

        Button(onClick = { isUpdateDialogVisible = true }) {
            Text("Update Marker")
        }

        OutlinedTextField(
            value = userNotes,
            onValueChange = { userNotes = it },
            label = { Text(text = "Notes", fontSize = fontSize.sp) },
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                // update the marker with the new notes
                val updatedMarker = marker.copy(notes = userNotes)
                onMarkerUpdated(updatedMarker)
            }
        ) {
            Text(text ="Save Notes", fontSize = fontSize.sp)
        }

        if (isUpdateDialogVisible) {
            CustomMarkerDialog(
                onDismissRequest = { isUpdateDialogVisible = false },
                onConfirm = { title, symbol ->
                    val updatedMarker = marker.copy(title = title, symbol = "$symbol.png")
                    onMarkerUpdated(updatedMarker)
                    isUpdateDialogVisible = false
                },
                existingMarker = marker
            )
        }
    }
}