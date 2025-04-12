package com.example.cs501_project.api

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