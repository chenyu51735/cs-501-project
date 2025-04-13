package com.example.cs501_project.api

import retrofit2.http.GET
import retrofit2.http.Query

// defines the contract of how the application will interact with the MediaWiki API
interface WikiApi {
    // used to get historical places near a specific (lat, lon) coordinates
    @GET("w/api.php?action=query&list=geosearch&format=json&gsradius=10000&gslimit=5")
    suspend fun searchNearbyHistoricalPlaces(
        @Query("gscoord") coordinates: String
    ): WikiResponse

    // used to get images from wikipedia of the historical places
    @GET("w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=500")
    suspend fun getPageImage(
        @Query("pageids") pageIds: String
    ): WikiImageResponse
}