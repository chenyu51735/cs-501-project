package com.example.cs501_project.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.cs501_project.model.HistoricalPlace

@Dao
interface HistoricalPlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historicalPlace: HistoricalPlace)

    @Query("SELECT * FROM historical_places WHERE userId = :userId")
    fun getAllHistoricalPlacesForUser(userId: Int): Flow<List<HistoricalPlace>>

    @Query("DELETE FROM historical_places WHERE userId = :userId")
    suspend fun deleteAllHistoricalPlacesForUser(userId: Int)
}