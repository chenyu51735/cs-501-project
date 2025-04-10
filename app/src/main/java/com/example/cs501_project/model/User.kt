package com.example.cs501_project.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// user class, will prompt user for full name, username, and password
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
)