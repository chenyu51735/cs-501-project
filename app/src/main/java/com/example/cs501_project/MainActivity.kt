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
import com.example.cs501_project.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: UserViewModel
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        setContent {
            Cs501projectTheme {
                navController = rememberNavController()
                AppNavigation(navController = navController, viewModel = viewModel)
            }
        }
    }
}
