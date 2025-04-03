package com.example.cs501_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cs501_project.ui.TravelApp
import com.example.cs501_project.ui.LoginForm
import com.example.cs501_project.ui.theme.Cs501projectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cs501projectTheme {
                    //TravelApp()
                LoginForm()
            }
        }
    }
}
