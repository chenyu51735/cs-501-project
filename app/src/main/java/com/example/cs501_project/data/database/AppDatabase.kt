package com.example.cs501_project.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cs501_project.model.CustomMarker
import com.example.cs501_project.model.HistoricalPlace
import com.example.cs501_project.model.Note
import com.example.cs501_project.model.User

@Database(entities = [User::class, HistoricalPlace::class, CustomMarker::class, Note::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun historicalPlaceDao(): HistoricalPlaceDao
    abstract fun customMarkerDao(): CustomMarkerDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}