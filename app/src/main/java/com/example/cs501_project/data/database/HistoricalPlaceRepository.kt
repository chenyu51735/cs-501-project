package com.example.cs501_project.data.database

import android.content.Context
import androidx.room.Room
import com.example.cs501_project.model.HistoricalPlace
import kotlinx.coroutines.flow.Flow

object HistoricalPlaceRepository {
    private var database: AppDatabase? = null

    fun initialize(context: Context) {
        if (database == null) {
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
        }
    }

    private val historicalPlaceDao by lazy {
        database?.historicalPlaceDao() ?: throw IllegalStateException("Database not initialized")
    }

    fun getHistoricalPlaces(): Flow<List<HistoricalPlace>> {
        return historicalPlaceDao.getAllHistoricalPlacesForUser(1)
    }

}