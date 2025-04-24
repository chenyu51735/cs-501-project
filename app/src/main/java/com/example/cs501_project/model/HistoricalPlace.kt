package com.example.cs501_project.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "historical_places",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ],
    indices = [androidx.room.Index(value = ["userId"])]
)
data class HistoricalPlace(
    @PrimaryKey val placeId: String, // unique id for place
    val userId: Int, // foreign key referencing the user id
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val historicalFacts: String,
    var pushNotificationSent: Boolean = false, // to track if a notification has been sent
    val imageUrl: String? = null
)