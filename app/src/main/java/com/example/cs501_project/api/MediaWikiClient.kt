package com.example.cs501_project.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// sets up and provides the singleton instance of Retrofit client, which is used to make api calls
object WikiClient { // ensures that only one instance of Retrofit client is used throughout app lifecycle
    private const val BASE_URL = "https://en.wikipedia.org/"

    val wikiApi: WikiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikiApi::class.java)
    }
}