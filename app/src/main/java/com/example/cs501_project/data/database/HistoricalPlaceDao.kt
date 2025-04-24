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

    @Query("SELECT * FROM historical_places WHERE placeId = :placeId AND userId = :userId")
    fun getHistoricalPlaceByIdForUser(placeId: String, userId: Int): Flow<HistoricalPlace?>

    @Query("UPDATE historical_places SET pushNotificationSent = 1 WHERE placeId = :placeId AND userId = :userId")
    suspend fun markAsNotifiedForUser(placeId: String, userId: Int)

    @Query("UPDATE historical_places SET historicalFacts = :facts WHERE placeId = :placeId AND userId = :userId")
    suspend fun updateFacts(placeId: String, userId: Int, facts: String?)
}