package com.example.cs501_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cs501_project.ui.CreateAccount
import com.example.cs501_project.ui.LandingScreen
import com.example.cs501_project.ui.LocationScreen
import com.example.cs501_project.ui.LoginForm
import com.example.cs501_project.viewmodel.UserViewModel

@Composable
fun AppNavigation(navController: NavHostController, viewModel: UserViewModel) {
    NavHost(
        // current start destination is the landing screen
        navController = navController,
        startDestination = "landingScreen"
    ) {
        composable(route = "landingScreen") {
            LandingScreen(onNavigate = { navController.navigate("login")} )
        }

        composable(route = "login") {
            LoginForm(viewModel = viewModel, onNavigateToCreateAccount = { navController.navigate("createAccount") }, onNavigateToLocationScreen = { navController.navigate("locationScreen") })
        }

        composable(route = "createAccount") {
            CreateAccount(viewModel = viewModel, onNavigateToLogin = { navController.popBackStack() })
        }

        composable(route = "locationScreen") {
            LocationScreen()
        }
    }
}