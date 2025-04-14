package com.example.cs501_project.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs501_project.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CreateAccount(viewModel: UserViewModel, onNavigateToLogin: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    // will take in viewmodel as a parameter from its call from LoginForm.kt to ensure data persistence
    val scope = rememberCoroutineScope()
    val users by viewModel.users.collectAsState(initial = emptyList()) // so we can observe all users and make real-time changes

    var newUsername by remember { mutableStateOf("") } // user will enter a preferred username and pw
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SnackbarHost(hostState = snackbarHostState)
        Text(
            text = "Welcome to Timeless Trails!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        OutlinedTextField(
            value = newUsername,
            onValueChange = { newUsername = it },
            label = { Text("Enter a Username") }
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Enter a Password") }
        )

        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // create account button
            Button( onClick = {
                // want to check if username already exists
                scope.launch {
                    val user = users.find { it.username == newUsername } // attempting to find user by username
                    if (user != null) {
                        // username is already taken
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Username already exists - please enter a new username")
                        }
                    } else {
                        // username is not taken, successfully created account
                        viewModel.addUser(newUsername, newPassword) // adding to the view model
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Account successfully created, redirecting you back to login")
                            delay(5000)
                        }
                        onNavigateToLogin()
                    }
                }
            }
            ) {
                Text(text = "Create")
            }
            Button(onClick = onNavigateToLogin) {
                Text(text = "Back")
            }
        }
    }
}