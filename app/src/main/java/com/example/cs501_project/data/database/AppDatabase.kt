package com.example.cs501_project.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cs501_project.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}