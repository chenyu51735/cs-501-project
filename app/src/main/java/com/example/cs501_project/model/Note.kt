package com.example.cs501_project.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val markerId: String,
    val noteText: String,
    val date: String,
    val photoUri: String? = null
)