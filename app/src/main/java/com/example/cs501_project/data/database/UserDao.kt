package com.example.cs501_project.data.database

import androidx.room.*
import com.example.cs501_project.model.User
import kotlinx.coroutines.flow.Flow

// data access object, defining all functions to be used on database
@Dao
interface UserDao {
    // returns all users as a flow to reflect real-time changes
    @Query("SELECT * FROM users ORDER BY id")
    fun getAll(): Flow<List<User>> // will retrieve all users

    // insert a user and replace with recent if conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    // update user record
    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)
}