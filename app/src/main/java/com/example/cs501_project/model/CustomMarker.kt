package com.example.cs501_project.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "custom_markers",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ],
    indices = [androidx.room.Index(value = ["userId"])]
)

data class CustomMarker(
    @PrimaryKey val markerId: String,
    val userId: Int, // user that it is associated with
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String, // image url for symbol
    val notes: String // user added notes
)