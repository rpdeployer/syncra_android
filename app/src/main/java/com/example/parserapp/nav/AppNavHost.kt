package com.example.parserapp.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parserapp.ui.screens.AddKeyScreen
import com.example.parserapp.ui.screens.AuthorizedScreen
import com.example.parserapp.ui.screens.ExitScreen
import com.example.parserapp.ui.screens.MainScreen
import com.example.parserapp.viewmodel.MainViewModel

@Composable
fun AppNavHost(navController: NavHostController, viewModel: MainViewModel) {
    val token by viewModel.apiKey.collectAsState(initial = null)

    val startDestination = when (token) {
        null -> null
        "" -> "main"
        else -> "authorized"
    }
    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("main") {
                MainScreen(navController, LocalContext.current, viewModel)
            }
            composable("addKey") {
                AddKeyScreen(navController)
            }
            composable("authorized") {
                AuthorizedScreen(navController, LocalContext.current)
            }
            composable("exit") {
                ExitScreen(navController, LocalContext.current, mainViewModel =  viewModel)
            }
        }
    }
}
