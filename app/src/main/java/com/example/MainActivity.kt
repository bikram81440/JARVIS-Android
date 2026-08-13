package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.JarvisTheme
import com.example.viewmodel.JarvisViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JarvisTheme {
                val navController = rememberNavController()
                val jarvisViewModel: JarvisViewModel = viewModel()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = jarvisViewModel,
                            onNavigateChat = { navController.navigate("chat") },
                            onNavigateHistory = { navController.navigate("history") },
                            onNavigateMemory = { navController.navigate("memory") },
                            onNavigateFiles = { navController.navigate("files") },
                            onNavigateSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("chat") {
                        ChatScreen(
                            viewModel = jarvisViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("history") {
                        HistoryScreen(
                            viewModel = jarvisViewModel,
                            onNavigateChat = { navController.navigate("chat") },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("memory") {
                        MemoryScreen(
                            viewModel = jarvisViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("files") {
                        FilesScreen(
                            viewModel = jarvisViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = jarvisViewModel,
                            onNavigateAbout = { navController.navigate("about") },
                            onNavigatePrivacy = { navController.navigate("privacy") },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("about") {
                        AboutScreen(onBack = { navController.popBackStack() })
                    }
                    composable("privacy") {
                        PrivacyScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
