package com.example.cs501_project.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _fontSize = MutableStateFlow(16f)
    val fontSize: StateFlow<Float> = _fontSize

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    fun setFontSize(size: Float) {
        _fontSize.value = size
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
    }
}
