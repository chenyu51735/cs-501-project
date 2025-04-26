package com.example.cs501_project.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cs501_project.ui.navigation.NavBar

@Composable
fun SettingsScreen(
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    darkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    navController: NavHostController
) {
    Scaffold(
        bottomBar = { NavBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)

            Column {
                Text(text = "Font size: ${fontSize.toInt()}sp")
                Slider(
                    value = fontSize,
                    onValueChange = onFontSizeChange,
                    valueRange = 12f..30f
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Dark mode")
                Switch(checked = darkMode, onCheckedChange = onDarkModeToggle)
            }
        }
    }
}