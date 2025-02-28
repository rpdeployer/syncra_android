package com.example.parserapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.parserapp.data.repository.AuthRepository
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.di.PermissionManager
import com.example.parserapp.nav.AppNavHost
import com.example.parserapp.ui.theme.ParserAppTheme
import com.example.parserapp.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParserAppTheme {
                val navController = rememberNavController()
                val viewModel = MainViewModel(
                    permissionManager = PermissionManager(LocalContext.current),
                    authRepository = AuthRepository(LocalContext.current),
                    dataStoreManager = DataStoreManager(LocalContext.current),
                )
                AppNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    ParserAppTheme {
        val navController = rememberNavController()
        val viewModel = MainViewModel(
            permissionManager = PermissionManager(LocalContext.current),
            authRepository = AuthRepository(LocalContext.current),
            dataStoreManager = DataStoreManager(LocalContext.current),
        )
        AppNavHost(
            navController = navController,
            viewModel = viewModel
        )
    }
}