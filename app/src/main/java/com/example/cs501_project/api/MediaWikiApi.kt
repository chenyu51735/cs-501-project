package com.example.cs501_project.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WikiApi {
    @GET("w/api.php?action=query&list=geosearch&format=json&gsradius=10000&gslimit=5")
    suspend fun searchNearbyHistoricalPlaces(
        @Query("gscoord") coordinates: String
    ): WikiResponse
}