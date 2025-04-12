package com.example.cs501_project.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs501_project.api.GeoSearchResult
import com.example.cs501_project.api.WikiClient
import com.example.cs501_project.location.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationService = LocationService(application.applicationContext)
    val currentLocation: StateFlow<Location?> = locationService.currentLocation

    private val _historicalPlaces = MutableStateFlow<List<GeoSearchResult>>(emptyList())
    val historicalPlaces: StateFlow<List<GeoSearchResult>> = _historicalPlaces

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationService.getLocationUpdates()
                .collectLatest { location ->
                    // location is already being updated in _currentLocation in LocationService
                    if (location != null) {
                        fetchNearbyHistoricalPlaces(location.latitude, location.longitude)
                    }
                }
        }
    }

    private fun fetchNearbyHistoricalPlaces(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val coordinates = "${latitude}|${longitude}"
                val response = WikiClient.wikiApi.searchNearbyHistoricalPlaces(coordinates)
                _historicalPlaces.value = response.query?.geosearch ?: emptyList()
            } catch (e: Exception) {
                // handle network errors
                e.printStackTrace()
                _historicalPlaces.value = listOf(GeoSearchResult(pageid = -1, ns = -1, title = "Error fetching data", lat = 0.0, lon = 0.0, dist = 0.0))
            }
        }
    }
}