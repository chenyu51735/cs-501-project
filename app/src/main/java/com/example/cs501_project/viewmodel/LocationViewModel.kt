package com.example.cs501_project.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs501_project.location.LocationService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationService = LocationService(application.applicationContext)
    val currentLocation: StateFlow<Location?> = locationService.currentLocation

    init {
        getLocation()
    }

    private fun getLocation() {
        viewModelScope.launch {
            locationService.getCurrentLocation() // this will update the currentLocation StateFlow
        }
    }
}