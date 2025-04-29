package com.example.cs501_project.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs501_project.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginForm(viewModel: UserViewModel,
              onNavigateToCreateAccount: () -> Unit,
              onNavigateToLocationScreen: (username: String) -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // for launching coroutines
    val users by viewModel.users.collectAsState(initial = emptyList()) // observing user list

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            // login button and functionality
            Button(onClick = {
                scope.launch {
                    val user = users.find { it.username == username } // attempting to find user by username
                    if (user != null && user.password == password) {
                        // login successful!
                        // navigate to their main screen
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Login successful")
                        }
                        onNavigateToLocationScreen(username)
                    }
                    if (user != null && user.password != password) {
                        // wrong password
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Password is incorrect, please try again")
                        }
                    }
                    if (user == null) {
                        // user does not exist, wrong username? or create account
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Username does not exist")
                        }
                    }
                }
            }) {
                Text(text = "Login")
            }

            TextButton(onClick = {
                // navigate to CreateAccount.kt
                onNavigateToCreateAccount()
            }) {
                Text(text = "New here?")
            }
        }
    }
}