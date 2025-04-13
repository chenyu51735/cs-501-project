package com.example.cs501_project.api

// defines the kotlin data classes that represent the structure of the JSON responses you expect to receive
data class WikiResponse(
    val query: Query?
)

data class Query(
    val geosearch: List<GeoSearchResult>?
)

data class GeoSearchResult(
    val pageid: Int,
    val ns: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    val dist: Double
)