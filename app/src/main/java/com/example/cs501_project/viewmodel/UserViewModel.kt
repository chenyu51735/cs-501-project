package com.example.cs501_project.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs501_project.data.database.DatabaseManager
import com.example.cs501_project.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// user view model that handles database operations and state tracking
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseManager.getDatabase(application).userDao()
    val users: Flow<List<User>> = dao.getAll() // observable list of users from flow

    private val _lastUpdatedId = MutableStateFlow<Int?>(null) // track last updated user ID
    val lastUpdatedId: StateFlow<Int?> = _lastUpdatedId

    // ddd new user
    fun addUser(username: String, password: String) {
        viewModelScope.launch {
            dao.insert(User(username = username, password = password))
        }
    }

    // update existing user
    fun updateUser(user: User) {
        viewModelScope.launch {
            dao.update(user)
            _lastUpdatedId.value = user.id // Mark this user as updated
            delay(2000) // Clear mark after 2 seconds
            _lastUpdatedId.value = null
        }
    }

    // delete user (ex: user deletes account)
    fun deleteUser(user: User) {
        viewModelScope.launch {
            dao.delete(user)
        }
    }
}