package com.example.cs501_project.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cs501_project.model.CustomMarker
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomMarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customMarker: CustomMarker)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customMapMarkers: List<CustomMarker>)

    @Query("SELECT * FROM custom_markers WHERE userId = :userId")
    fun getCustomMarkersForUser(userId: Int): Flow<List<CustomMarker>>
}