package com.example.cs501_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.cs501_project.ui.TravelApp
import com.example.cs501_project.ui.LoginForm
import com.example.cs501_project.ui.CreateAccount
import com.example.cs501_project.ui.theme.Cs501projectTheme
import com.example.cs501_project.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        setContent {
            Cs501projectTheme {
                    //TravelApp()
                CreateAccount(viewModel = viewModel)
            }
        }
    }
}
