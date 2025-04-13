package com.example.cs501_project.viewmodel

import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
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
import java.util.Locale

// intermediary between LocationScreen and data sources (MediaWikiClient and LocationService)
class LocationViewModel(application: Application) : AndroidViewModel(application) {
    // needed to access location updates
    private val locationService = LocationService(application.applicationContext)
    // using locationService to access the latest location
    val currentLocation: StateFlow<Location?> = locationService.currentLocation

    // mutable stateflow to hold the list of historical places from mediawiki using geo-searching
    private val _historicalPlaces = MutableStateFlow<List<GeoSearchResult>>(emptyList())
    // immutable stateflow for other parts of app to access
    val historicalPlaces: StateFlow<List<GeoSearchResult>> = _historicalPlaces

    private val _currentCity = MutableStateFlow<String?>(null)
    val currentCity: StateFlow<String?> = _currentCity

    // once the viewmodel is created, it starts listening for location updates from LocationService
    init {
        startLocationUpdates()
    }

    // launches a coroutine that collects the Flow of location updates from LocationService
    // whenever a new location is received, it will fetch nearby historical places for that location
    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationService.getLocationUpdates()
                .collectLatest { location ->
                    // location is already being updated in _currentLocation in LocationService
                    if (location != null) {
                        fetchNearbyHistoricalPlaces(location.latitude, location.longitude)
                        val cityName = reverseGeocode(getApplication(), location.latitude, location.longitude)
                        _currentCity.value = cityName.toString()
                    }
                }
        }
    }

    // launches a coroutine to make an api call to MediaWiki to search for historical places near coordinates
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

    // reverse geocoding function to get city from latitude and longitude
    private fun reverseGeocode(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].locality
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}