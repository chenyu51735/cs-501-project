package com.example.cs501_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cs501_project.ui.navigation.AppNavigation
import com.example.cs501_project.ui.theme.Cs501projectTheme
import com.example.cs501_project.viewmodel.LocationViewModel
import com.example.cs501_project.viewmodel.SettingsViewModel
import com.example.cs501_project.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var navController: NavHostController
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        setContent {
            val darkMode by settingsViewModel.darkMode.collectAsState()

            Cs501projectTheme (darkTheme = darkMode) {
                navController = rememberNavController()
                AppNavigation(navController = navController, userViewModel = userViewModel, locationViewModel = locationViewModel, settingsViewModel = settingsViewModel)
            }
        }
    }
}
