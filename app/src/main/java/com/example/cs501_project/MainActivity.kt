package com.example.cs501_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cs501_project.ui.navigation.AppNavigation
import com.example.cs501_project.ui.theme.Cs501projectTheme
import com.example.cs501_project.viewmodel.LocationViewModel
import com.example.cs501_project.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        setContent {
            Cs501projectTheme {
                navController = rememberNavController()
                AppNavigation(navController = navController, userViewModel = userViewModel, locationViewModel = locationViewModel)
            }
        }
    }
}
