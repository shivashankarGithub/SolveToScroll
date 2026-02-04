package com.solvetoscroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solvetoscroll.ui.screens.AddAppScreen
import com.solvetoscroll.ui.screens.HomeScreen
import com.solvetoscroll.ui.screens.HomeViewModel
import com.solvetoscroll.ui.screens.ScheduleScreen
import com.solvetoscroll.ui.screens.SettingsScreen
import com.solvetoscroll.ui.onboarding.OnboardingScreen
import com.solvetoscroll.ui.theme.SolveToScrollTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            SolveToScrollTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val hasCompletedOnboarding by homeViewModel.hasCompletedOnboarding.collectAsState()
                    
                    val startDestination = if (hasCompletedOnboarding) "home" else "onboarding"
                    
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(
                                onComplete = {
                                    homeViewModel.completeOnboarding()
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("home") {
                            HomeScreen(
                                onAddApp = { navController.navigate("add_app") },
                                onSettings = { navController.navigate("settings") },
                                onEditSchedule = { packageName ->
                                    navController.navigate("schedule/$packageName")
                                }
                            )
                        }
                        
                        composable("add_app") {
                            AddAppScreen(
                                onAppSelected = { packageName ->
                                    navController.navigate("schedule/$packageName")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("schedule/{packageName}") { backStackEntry ->
                            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
                            ScheduleScreen(
                                packageName = packageName,
                                onSave = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
