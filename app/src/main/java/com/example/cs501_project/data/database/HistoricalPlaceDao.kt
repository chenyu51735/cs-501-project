package com.example.cs501_project.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.cs501_project.model.HistoricalPlace

@Dao
interface HistoricalPlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historicalPlace: HistoricalPlace)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(historicalPlaces: List<HistoricalPlace>)

    @Query("SELECT * FROM historical_places WHERE userId = :userId")
    fun getAllHistoricalPlacesForUser(userId: Int): Flow<List<HistoricalPlace>>

    @Query("UPDATE historical_places SET pushNotificationSent = 1 WHERE placeId = :placeId AND userId = :userId")
    suspend fun markAsNotifiedForUser(placeId: String, userId: Int)

    @Query("DELETE FROM historical_places WHERE userId = :userId")
    suspend fun deleteAllHistoricalPlacesForUser(userId: Int)
}