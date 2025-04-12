package com.example.cs501_project.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WikiClient {
    private const val BASE_URL = "https://en.wikipedia.org/"

    val wikiApi: WikiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikiApi::class.java)
    }
}