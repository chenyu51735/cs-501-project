package com.example.cs501_project.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.cs501_project.model.TravelEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TravelViewModel : ViewModel() {
    private val _entries = MutableStateFlow<List<TravelEntry>>(emptyList())
    val entries: StateFlow<List<TravelEntry>> = _entries

    fun addEntry(notes: String, location: Location?) {
        val lat = location?.latitude ?: 0.0
        val lon = location?.longitude ?: 0.0
        val title = "Visited Location ($lat, $lon)"
        val newEntry = TravelEntry(title, notes)
        _entries.value += newEntry
    }
}
