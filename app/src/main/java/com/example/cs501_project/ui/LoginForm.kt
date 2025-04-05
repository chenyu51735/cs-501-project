package com.example.cs501_project.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs501_project.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginForm(viewModel: UserViewModel) {
    val scope = rememberCoroutineScope() // for launching coroutines
    val users by viewModel.users.collectAsState(initial = emptyList()) // observing user list

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
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
            label = { Text("Password") }
        )

        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            // login button and functionality
            Button(onClick = {
                // check if the account exists
                // if it does not exist, pop up a DNE and to create an account
                // if it does, success! login
                scope.launch {
                    val user = users.find { it.username === username } // attempting to find user by username
                    if (user != null && user.password === password) {
                        // login successful!
                        // navigate to their main screen
                    }
                }
            }) {
                Text(text = "Login")
            }

            // new create account and functionality
            TextButton(onClick = {
                // bring to a new screen if they are new
            }) {
                Text(text = "New here?")
            }
        }
    }
}