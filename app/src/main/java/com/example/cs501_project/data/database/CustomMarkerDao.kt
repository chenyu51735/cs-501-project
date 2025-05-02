package com.example.cs501_project.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cs501_project.model.CustomMarker

@Dao
interface CustomMarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customMarker: CustomMarker)

    @Query("SELECT * FROM custom_markers WHERE userId = :userId")
    suspend fun getCustomMarkersForUserList(userId: Int): List<CustomMarker>
}