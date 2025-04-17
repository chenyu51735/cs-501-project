package com.example.cs501_project.ui.navigation

import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cs501_project.ui.CreateAccount
import com.example.cs501_project.ui.HistoricalFacts
import com.example.cs501_project.ui.LandingScreen
import com.example.cs501_project.ui.LocationScreen
import com.example.cs501_project.ui.LoginForm
import com.google.gson.Gson
import com.example.cs501_project.viewmodel.HistoricalPlaceWithImage
import com.example.cs501_project.viewmodel.LocationViewModel
import com.example.cs501_project.viewmodel.UserViewModel

@Composable
fun AppNavigation(navController: NavHostController, userViewModel: UserViewModel, locationViewModel: LocationViewModel) {
    val gson = Gson()
    NavHost(
        // current start destination is the landing screen
        navController = navController,
        startDestination = "landingScreen"
    ) {
        composable(route = "landingScreen") {
            LandingScreen(onNavigate = { navController.navigate("login")} )
        }

        composable(route = "login") {
            LoginForm(viewModel = userViewModel, onNavigateToCreateAccount = { navController.navigate("createAccount") }, onNavigateToLocationScreen = { navController.navigate("locationScreen") })
        }

        composable(route = "createAccount") {
            CreateAccount(viewModel = userViewModel, onNavigateToLogin = { navController.popBackStack() })
        }

        composable(route = "locationScreen") {
            LocationScreen(
                locationViewModel = locationViewModel,
                onNavigateToFacts = { historicalPlace -> // Receive the HistoricalPlaceWithImage
                    val encodedPlace = Uri.encode(gson.toJson(historicalPlace))
                    navController.navigate("historicalInfo/$encodedPlace")
                }
            )
        }

        composable(
            route = "historicalInfo/{historicalPlace}",
            arguments = listOf(
                navArgument("historicalPlace") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val historicalPlaceJson = backStackEntry.arguments?.getString("historicalPlace")
            if (!historicalPlaceJson.isNullOrEmpty()) {
                val historicalPlace = gson.fromJson(historicalPlaceJson, HistoricalPlaceWithImage::class.java)
                HistoricalFacts(place = historicalPlace)
            } else {
                Text("Error: Historical place data not found.") // handle potential error
            }
        }
    }
}