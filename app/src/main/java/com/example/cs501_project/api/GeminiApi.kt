package com.example.cs501_project.api

import com.example.cs501_project.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel


class GeminiApi {
    private val geminiModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.gemini_api_key
    )

    suspend fun getHistoricalFacts(place: String): String? {
        val prompt = "Tell me five interesting facts about $place and use periods to separate them instead of bullet points"
        val response = geminiModel.generateContent(prompt)
        return response.text
    }
}