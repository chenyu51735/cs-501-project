package com.example.cs501_project.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.cs501_project.viewmodel.CustomMapMarker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NoteEntry(val note: String, val date: String, val photoUri: Uri? = null)

// when a user clicks on the card of their own custom marked location, lead them to this screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLocationDetails(
    marker: CustomMapMarker,
    onMarkerUpdated: (CustomMapMarker) -> Unit, // callback to send marker back
    navController: NavHostController
) {
    var isUpdateDialogVisible by remember { mutableStateOf(false) }
    var newNote by remember { mutableStateOf("") }
    val notesState = rememberSaveable(
        stateSaver = listSaver(
            save = { notes ->
                notes.flatMap { listOf(it.note, it.date, it.photoUri?.toString() ?: "") }
            },
            restore = { flatList ->
                flatList.chunked(3).map { chunk ->
                    NoteEntry(chunk[0], chunk[1], if (chunk[2].isNotEmpty()) Uri.parse(chunk[2]) else null)
                }
            }
        )
    ) { mutableStateOf(emptyList<NoteEntry>()) }
    val notes = notesState.value


    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Back bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(marker.title, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        )
        {

            // Edit button
            Button(
                onClick = { isUpdateDialogVisible = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Marker",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Edit Title or Symbol", fontWeight = FontWeight.Medium)
            }


            OutlinedTextField(
                value = newNote,
                onValueChange = { newNote = it },
                label = { Text("Add a new note") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {

                // Attach image button
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach Photo",
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Attach Photo")
                }

                // Save button
                Button(
                    onClick = {
                        if (newNote.isNotBlank()) {
                            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                            val newEntry = NoteEntry(newNote, currentDate, selectedImageUri)
                            notesState.value = notes + newEntry
                            newNote = ""
                            selectedImageUri = null
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save Note",
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Save Note")
                }

            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Your Notes", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            // List of notes
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(notes) { entry ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = entry.date, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = entry.note)

                        entry.photoUri?.let { uri ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Attached photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    if (isUpdateDialogVisible) {
        CustomMarkerDialog(
            onDismissRequest = { isUpdateDialogVisible = false },
            onConfirm = { newTitle, newSymbol ->
                val updated = marker.copy(title = newTitle, symbol = "$newSymbol.png")
                onMarkerUpdated(updated)
                isUpdateDialogVisible = false
            },
            existingMarker = marker
        )
    }
}