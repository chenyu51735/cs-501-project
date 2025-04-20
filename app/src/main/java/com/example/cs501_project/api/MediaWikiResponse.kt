package com.example.cs501_project.api

// defines the kotlin data classes that represent the structure of the JSON responses you expect to receive
data class WikiResponse(
    val query: Query?
)

data class Query(
    val geosearch: List<GeoSearchResult>?
)

data class GeoSearchResult(
    val pageid: Int, // unique identifier for each page
    val ns: Int, // namespace of page
    val title: String,
    val lat: Double,
    val lon: Double,
    val dist: Double // distance in meters from your provided coordinates in query
)

data class WikiImageResponse(
    val query: ImageQuery?
)

data class ImageQuery(
    val pages: Map<String, ImagePage>?
)

data class ImagePage(
    val pageid: Int,
    val thumbnail: Thumbnail?
)

data class Thumbnail(
    val source: String,
    val width: Int,
    val height: Int
)

data class WikiExtractResponse(
    val batchcomplete: String? = null, // will indicate if the batch query was successful
    val query: ExtractQuery? = null // instance of ExtractQuery class that contains extract info
)

data class ExtractQuery(
    val normalized: List<NormalizedPage>? = null, // optional list of pages that were normalized
    val pages: Map<String, PageExtract>? = null // String key is the pageId
)

data class NormalizedPage( // representing a normalized page
    val from: String? = null,
    val to: String? = null
)

data class PageExtract(
    val pageid: Int? = null,
    val ns: Int? = null,
    val title: String? = null,
    val extract: String? = null // this is where the historical fact will be
)