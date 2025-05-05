package com.example.cs501_project.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs501_project.data.database.AppDatabase
import com.example.cs501_project.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// user view model that handles database operations and state tracking
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).userDao()
    val users: Flow<List<User>> = dao.getAll() // observable list of users from flow

    // add new user
    fun addUser(username: String, password: String) {
        viewModelScope.launch {
            dao.insert(User(username = username, password = password))
        }
    }
}