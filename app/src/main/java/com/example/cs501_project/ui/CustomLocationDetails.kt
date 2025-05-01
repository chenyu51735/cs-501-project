package com.example.cs501_project.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material3.TextButton
import coil.compose.rememberAsyncImagePainter
import com.example.cs501_project.model.Note
import com.example.cs501_project.viewmodel.CustomMapMarker
import com.example.cs501_project.viewmodel.LocationViewModel
import com.example.cs501_project.viewmodel.SettingsViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// when a user clicks on the card of their own custom marked location, lead them to this screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLocationDetails(
    marker: CustomMapMarker,
    onMarkerUpdated: (CustomMapMarker) -> Unit, // callback to send marker back
    navController: NavHostController,
    viewModel: LocationViewModel,
    settingsViewModel: SettingsViewModel
) {
    var isUpdateDialogVisible by remember { mutableStateOf(false) }
    var newNote by remember { mutableStateOf("") }
    val markerId = marker.point.toString()
    val notes by viewModel.getNotesForMarker(markerId).collectAsState()
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    val fontSize = settingsViewModel.fontSize.collectAsState().value.sp
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    // Image saving
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Copy image to internal storage
            val fileName = "note_image_${System.currentTimeMillis()}.jpg"
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val outputFile = File(context.filesDir, fileName)
            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            selectedImageUri = Uri.fromFile(outputFile)
        }
    }

    // Back bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(marker.title, fontSize = fontSize, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (screenWidthDp < 600) {
            // Phone view
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Edit button
                Button(
                    onClick = { isUpdateDialogVisible = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Marker", modifier = Modifier.padding(end = 8.dp))
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
                    Button(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Attach Photo", modifier = Modifier.padding(end = 6.dp))
                        Text("Attach Photo")
                    }

                    Button(
                        onClick = {
                            if (newNote.isNotBlank()) {
                                val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                                viewModel.addNote(
                                    markerId = markerId,
                                    noteText = newNote,
                                    date = currentDate,
                                    photoUri = selectedImageUri?.toString()
                                )
                                newNote = ""
                                selectedImageUri = null
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note", modifier = Modifier.padding(end = 6.dp))
                        Text("Save Note")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Notes", fontWeight = FontWeight.SemiBold, fontSize = fontSize)

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(notes) { entry ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(entry.date, style = MaterialTheme.typography.labelSmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(entry.noteText)
                                }

                                IconButton(onClick = { noteToDelete = entry }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                                }
                            }

                            entry.photoUri?.let { uri ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Image(
                                    painter = rememberAsyncImagePainter(Uri.parse(uri)),
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
        } else {
            // Tablet view, left editing, right notes list
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left side: editing
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Button(
                        onClick = { isUpdateDialogVisible = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Marker", modifier = Modifier.padding(end = 8.dp))
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
                        Button(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Attach Photo", modifier = Modifier.padding(end = 6.dp))
                            Text("Attach Photo")
                        }

                        Button(
                            onClick = {
                                if (newNote.isNotBlank()) {
                                    val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                                    viewModel.addNote(
                                        markerId = markerId,
                                        noteText = newNote,
                                        date = currentDate,
                                        photoUri = selectedImageUri?.toString()
                                    )
                                    newNote = ""
                                    selectedImageUri = null
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save Note", modifier = Modifier.padding(end = 6.dp))
                            Text("Save Note")
                        }
                    }
                }
                // Right side: note list
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // "Your Notes" label at top-right
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Your Notes", fontWeight = FontWeight.SemiBold, fontSize = fontSize)
                    }

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(notes) { entry ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            entry.date, style = MaterialTheme.typography.labelSmall
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(entry.noteText)
                                    }

                                    IconButton(onClick = { noteToDelete = entry }) {
                                        Icon(
                                            Icons.Default.Delete, contentDescription = "Delete Note"
                                        )
                                    }
                                }

                                entry.photoUri?.let { uri ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Image(
                                        painter = rememberAsyncImagePainter(Uri.parse(uri)),
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

            // Delete confirmation popup
            noteToDelete?.let { note ->
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { noteToDelete = null },
                    title = { Text("Delete Note") },
                    text = { Text("Are you sure you want to delete this note?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteNote(note)
                                noteToDelete = null
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { noteToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
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
    }
}
