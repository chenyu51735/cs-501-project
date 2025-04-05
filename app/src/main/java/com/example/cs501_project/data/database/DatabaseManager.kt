package com.example.cs501_project.data.database

import android.content.Context

object DatabaseManager {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (database == null) {
            synchronized(this) {
                database = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_db"
                ).build()
            }
        }
        return database!!
    }
}