package com.example.cs501_project.ui.navigation

import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cs501_project.ui.CreateAccount
import com.example.cs501_project.ui.CustomLocationDetails
import com.example.cs501_project.ui.HistoricalFacts
import com.example.cs501_project.ui.LandingScreen
import com.example.cs501_project.ui.LocationScreen
import com.example.cs501_project.ui.LoginForm
import com.example.cs501_project.ui.SettingsScreen
import com.example.cs501_project.viewmodel.CustomMapMarker
import com.example.cs501_project.viewmodel.HistoricalPlaceWithImage
import com.example.cs501_project.viewmodel.LocationViewModel
import com.example.cs501_project.viewmodel.SettingsViewModel
import com.example.cs501_project.viewmodel.UserViewModel
import com.google.gson.Gson

@Composable
fun AppNavigation(navController: NavHostController, userViewModel: UserViewModel, locationViewModel: LocationViewModel, settingsViewModel: SettingsViewModel) {
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
                settingsViewModel = settingsViewModel,
                onNavigateToFacts = { historicalPlace -> // Receive the HistoricalPlaceWithImage
                    val encodedPlace = Uri.encode(gson.toJson(historicalPlace))
                    navController.navigate("historicalInfo/$encodedPlace")
                },
                navController = navController
            )
        }

        composable(route = "settingsScreen") {
            SettingsScreen(
                fontSize = settingsViewModel.fontSize.collectAsState().value,
                onFontSizeChange = { settingsViewModel.setFontSize(it) },
                darkMode = settingsViewModel.darkMode.collectAsState().value,
                onDarkModeToggle = { settingsViewModel.toggleDarkMode(it) },
                settingsViewModel = settingsViewModel,
                navController = navController,

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
                HistoricalFacts(place = historicalPlace, navController = navController, settingsViewModel = settingsViewModel)
            } else {
                Text("Error: Historical place data not found.") // handle potential error
            }
        }

        composable(
            route = "customCardDetails/{customMarker}",
            arguments = listOf(
                navArgument("customMarker") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val customMarkerJson = backStackEntry.arguments?.getString("customMarker")

            if (!customMarkerJson.isNullOrEmpty()) {
                val customMarker = gson.fromJson(customMarkerJson, CustomMapMarker::class.java)
                val previousBackStackEntry = navController.previousBackStackEntry

                CustomLocationDetails(
                    marker = customMarker,
                    onMarkerUpdated = { updatedMarker ->
                        val result = Uri.encode(gson.toJson(updatedMarker))
                        previousBackStackEntry?.savedStateHandle?.set("updatedCustomMarker", result)
                        navController.popBackStack()
                    },
                    navController = navController,
                    viewModel = locationViewModel,
                    settingsViewModel = settingsViewModel
                    )
            } else {
                Text("Error: Custom marker data not found.")
            }
        }
    }
}