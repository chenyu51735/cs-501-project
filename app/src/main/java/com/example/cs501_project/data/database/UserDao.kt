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

    // get username from user id
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUsernameFromUserId(userId: Int): User

    // insert a user and replace with recent if conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)
}