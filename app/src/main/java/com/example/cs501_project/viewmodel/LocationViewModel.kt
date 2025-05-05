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
import com.example.cs501_project.location.LocationService
import com.example.cs501_project.model.CustomMarker
import com.example.cs501_project.model.HistoricalPlace
import com.example.cs501_project.model.Note
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

// this data class will map the geo search result (historical place) with the url of an image of it
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
    val symbol: String, // name of symbol without .png at end
    val notes: String
)

// intermediary between LocationScreen and data sources (MediaWikiClient and LocationService)
class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val historicalPlaceDao = AppDatabase.getDatabase(application).historicalPlaceDao()
    private val customMarkerDao = AppDatabase.getDatabase(application).customMarkerDao()
    private val userDao = AppDatabase.getDatabase(application).userDao()
    // needed to access location updates
    private val locationService = LocationService(application.applicationContext)
    // using locationService to access the latest location
    val currentLocation: StateFlow<Location?> = locationService.currentLocation
    // gemini api instance
    private val geminiApi = GeminiApi()
    // noteDao
    private val noteDao = AppDatabase.getDatabase(application).noteDao()

    // custom map markers
    private val _customMarkers = MutableStateFlow<List<CustomMapMarker>>(emptyList())
    val customMarkers: StateFlow<List<CustomMapMarker>> = _customMarkers

    private val currentUserId = 1 // hard-coded user id because there's only ever one user in database

    var currentUsername: String = ""

    // once the viewmodel is created, it starts listening for location updates from LocationService
    init {
        startLocationUpdates()
        loadCustomMarkers()
        loadUsername()
    }
    fun getNotesForMarker(markerId: String): StateFlow<List<Note>> {
        return noteDao.getNotes(markerId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addNote(markerId: String, noteText: String, date: String, photoUri: String?) {
        viewModelScope.launch {
            val note = Note(
                markerId = markerId,
                noteText = noteText,
                date = date,
                photoUri = photoUri
            )
            noteDao.insert(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    private fun loadUsername() {
        viewModelScope.launch {
            val user = userDao.getUsernameFromUserId(1)
            currentUsername = user.username
        }
    }

    private fun loadCustomMarkers() {
        viewModelScope.launch {
            val customMarkerEntities = customMarkerDao.getCustomMarkersForUserList(currentUserId)
            val mapMarkers = customMarkerEntities.map { entity ->
                CustomMapMarker(
                    point = Point.fromLngLat(entity.longitude, entity.latitude),
                    title = entity.title,
                    symbol = entity.imageUrl,
                    notes = entity.notes
                )
            }
            _customMarkers.value = mapMarkers
        }
    }


    // updating list of the markers
    fun addCustomMarker(point: Point, title: String, symbol: String) {
        viewModelScope.launch {
            val markerId = point.toString() // markerId will just be the point as a string
            val newCustomMarker = CustomMarker(
                markerId = markerId,
                userId = currentUserId,
                title = title,
                latitude = point.latitude(),
                longitude = point.longitude(),
                imageUrl = symbol,
                notes = "" // empty initially
            )
            customMarkerDao.insert(newCustomMarker)
            _customMarkers.update { currentList ->
                currentList + CustomMapMarker(point = point, title = title, symbol = symbol, notes = "")
            }
        }
    }

    // updating a specific marker
    fun updateCustomMarker(updatedMarker: CustomMapMarker) {
        viewModelScope.launch {
            val markerToUpdate = CustomMarker(
                markerId = updatedMarker.point.toString(),
                userId = currentUserId,
                title = updatedMarker.title,
                latitude = updatedMarker.point.latitude(),
                longitude = updatedMarker.point.longitude(),
                imageUrl = updatedMarker.symbol,
                notes = updatedMarker.notes
            )
            customMarkerDao.insert(markerToUpdate)
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
    }

    private val _currentCity = MutableStateFlow<String?>(null)
    val currentCity: StateFlow<String?> = _currentCity

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
                        historicalFacts = entity.historicalFacts!!.split(".")
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // launches a coroutine that collects the Flow of location updates from LocationService
    // whenever a new location is received, it will fetch nearby historical places for that location
    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationService.getLocationUpdates()
                .collectLatest { location ->
                    // location is already being updated in _currentLocation in LocationService
                    if (location != null) {
                        fetchNearbyHistoricalPlaces(location.latitude, location.longitude)
                        val cityName = getCityFromCoordinates(location.latitude, location.longitude)
                        _currentCity.value = cityName
                    }
                }
        }
    }

    // launches a coroutine to make an api call to MediaWiki to search for historical places near coordinates
    private fun fetchNearbyHistoricalPlaces(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                historicalPlaceDao.deleteAllHistoricalPlacesForUser(currentUserId)
                val coordinates = "${latitude}|${longitude}"
                val response = WikiClient.wikiApi.searchNearbyHistoricalPlaces(coordinates)
                val places = response.query?.geosearch ?: emptyList()

                places.forEach { place ->
                    // fetching the image
                    var imageUrl: String? = null // default to null if no image
                    try {
                        val imageResponse = WikiClient.wikiApi.getPageImage(pageIds = place.pageid.toString())
                        imageUrl = imageResponse.query?.pages?.get(place.pageid.toString())?.thumbnail?.source
                    } catch (e: Exception) {
                        e.printStackTrace() // Log the error, but continue
                    }

                    // fetching facts
                    var facts: String? = null
                    try {
                        val geminiResponse = geminiApi.getHistoricalFacts(place.title)
                        facts = geminiResponse?.split(".")?.filter { it.isNotBlank() }?.joinToString(".")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val historicalPlaceEntity = HistoricalPlace(
                        userId = currentUserId,
                        placeId = place.pageid.toString(), // use pageId as a stable ID
                        title = place.title,
                        latitude = place.lat,
                        longitude = place.lon,
                        historicalFacts = facts,
                        pushNotificationSent = false,
                        imageUrl = imageUrl
                    )
                    historicalPlaceDao.insert(historicalPlaceEntity)
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
                val address = addresses[0]

                val addressParts = with(address) {
                    listOfNotNull(
                        thoroughfare, // street name
                        subThoroughfare, // street number
                        locality,      // city
                        adminArea,     // state/province
                        postalCode,    // postal code
                        countryName    // country
                    )
                }
                addressParts.joinToString(", ")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // to get street address of each suggested location
    fun getStreetAddressFromCoordinates(latitude: Double, longitude: Double): String? {
        return reverseGeocode(getApplication(), latitude, longitude)
    }

    // for current city display purposes
    private fun getCityFromCoordinates(latitude: Double, longitude: Double): String {
        val fullAddress = reverseGeocode(getApplication(), latitude, longitude)
        return fullAddress!!.split(",")[2].trim()
    }
}

