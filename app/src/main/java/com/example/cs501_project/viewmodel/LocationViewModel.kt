package com.example.cs501_project.viewmodel

import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs501_project.api.GeminiApi
import com.example.cs501_project.api.GeoSearchResult
import com.example.cs501_project.api.WikiClient
import com.example.cs501_project.data.database.AppDatabase
import com.example.cs501_project.data.database.HistoricalPlaceDao
import com.example.cs501_project.location.LocationService
import com.example.cs501_project.model.HistoricalPlace
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Locale

// this data class will map the geosearch result (historical place) with the url of an image of it
data class HistoricalPlaceWithImage(
    // historical location suggestions as GeoSearchResult
    val geoSearchResult: GeoSearchResult,
    // image URL of the location
    val imageUrl: String? = null,
    // any historical facts associated with it from the MediaWiki API
    val historicalFacts: List<String> = emptyList()
)

// data class for the custom map markers when user clicks map
data class CustomMapMarker(
    val point: Point, // lat and lon point
    val title: String, // whatever the user wants to call it
    val symbol: String // name of symbol without .png at end
)

// intermediary between LocationScreen and data sources (MediaWikiClient and LocationService)
class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val historicalPlaceDao = AppDatabase.getDatabase(application).historicalPlaceDao()
    // needed to access location updates
    private val locationService = LocationService(application.applicationContext)
    // using locationService to access the latest location
    val currentLocation: StateFlow<Location?> = locationService.currentLocation
    // gemini api instance
    private val geminiApi = GeminiApi()

    // custom map markers
    private val _customMarkers = MutableStateFlow<List<CustomMapMarker>>(emptyList())
    val customMarkers: StateFlow<List<CustomMapMarker>> = _customMarkers

    // updating list of the markers
    fun addCustomMarker(point: Point, title: String, symbol: String) {
        _customMarkers.update { currentList ->
            currentList + CustomMapMarker(point = point, title = title, symbol = symbol)
        }
    }

    // updating a specific marker
    fun updateCustomMarker(updatedMarker: CustomMapMarker) {
        _customMarkers.update { currentList ->
            currentList.map {
                if (it.point == updatedMarker.point) { // assuming point is a unique identifier
                    updatedMarker
                } else {
                    it
                }
            }
        }
    }

    private val _currentCity = MutableStateFlow<String?>(null)
    val currentCity: StateFlow<String?> = _currentCity

    private val currentUserId: Int = 1 // Replace with your actual user ID retrieval NEED TO CHANGE, maybe after remote database auth?

    val historicalPlaces: StateFlow<List<HistoricalPlaceWithImage>> =
        historicalPlaceDao.getAllHistoricalPlacesForUser(currentUserId)
            .map { entities ->
                entities.map { entity ->
                    HistoricalPlaceWithImage(
                        geoSearchResult = GeoSearchResult(
                            pageid = entity.placeId.hashCode(), // Use a stable unique ID
                            ns = 0,
                            title = entity.title,
                            lat = entity.latitude,
                            lon = entity.longitude,
                            dist = 0.0
                        ),
                        imageUrl = entity.imageUrl,
                        historicalFacts = emptyList()
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

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
        Log.d("LocationViewModel", "fetchNearbyHistoricalPlaces called with: $latitude, $longitude")
        viewModelScope.launch {
            try {
                val coordinates = "${latitude}|${longitude}"
                Log.d("LocationViewModel", "Calling WikiClient API with coordinates: $coordinates")
                val response = WikiClient.wikiApi.searchNearbyHistoricalPlaces(coordinates)
                Log.d("LocationViewModel", "WikiClient API response: $response")
                val places = response.query?.geosearch ?: emptyList()
                Log.d("LocationViewModel", "Number of places found: ${places.size}")
                places.forEach { place ->
                    var imageUrl: String? = null // default to null if no image
                    try {
                        val imageResponse = WikiClient.wikiApi.getPageImage(pageIds = place.pageid.toString())
                        imageUrl = imageResponse.query?.pages?.get(place.pageid.toString())?.thumbnail?.source
                    } catch (e: Exception) {
                        e.printStackTrace() // Log the error, but continue
                    }
                    Log.d(
                        "LocationViewModel",
                        "Place: title=${place.title}, pageid=${place.pageid}, lat=${place.lat}, lon=${place.lon}"
                    )
                    val historicalPlaceEntity = HistoricalPlace(
                        userId = currentUserId,
                        placeId = place.pageid.toString(), // use pageid as a stable ID
                        title = place.title,
                        latitude = place.lat,
                        longitude = place.lon,
                        historicalFacts = null.toString(), // fetch facts later
                        pushNotificationSent = false,
                        imageUrl = imageUrl
                    )
                    historicalPlaceDao.insert(historicalPlaceEntity)
                    fetchFactsForPlaces(historicalPlaceEntity)
                }
            } catch (e: Exception) {
                // handle network errors
                Log.e("LocationViewModel", "Error fetching nearby places: ${e.message}")
                e.printStackTrace()
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

    private fun fetchFactsForPlaces(historicalPlace: HistoricalPlace) {
        viewModelScope.launch {
            try {
                val response = geminiApi.getHistoricalFacts(historicalPlace.title)
                val facts = response?.split(".")?.filter { it.isNotBlank() }?.joinToString(".")
                historicalPlaceDao.updateFacts(historicalPlace.placeId, currentUserId, facts)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun markHistoricalPlaceAsNotified(placeId: String) {
        historicalPlaceDao.markAsNotifiedForUser(placeId, currentUserId)
    }
}

