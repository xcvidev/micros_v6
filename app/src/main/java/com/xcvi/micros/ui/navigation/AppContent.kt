package com.xcvi.micros.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xcvi.micros.ui.navigation.FoodDestination.foodGraph
import com.xcvi.micros.ui.navigation.MessageDestination.messageGraph
import com.xcvi.micros.ui.navigation.WeightDestination.weightGraph
import com.xcvi.micros.ui.screens.dashboard.DashboardViewModel
import com.xcvi.micros.ui.screens.message.MessageViewModel
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppContent() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    val mainRoutes = listOf(
        FoodDestination.ROUTE,
        MessageDestination.ROUTE,
        WeightDestination.ROUTE,
        // SettingsDestination.ROUTE,
    )
    val showBottomBar = currentDestination in mainRoutes

    val dashboardViewModel: DashboardViewModel = koinViewModel()    // scoped to activity
    val assistantViewModel: MessageViewModel = koinViewModel()    // scoped to activity

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentDestination
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Graphs.FOOD
        ) {
            foodGraph(navController, dashboardViewModel)
            messageGraph(navController, assistantViewModel)
            weightGraph(/*navController*/)
            /*settingsGraph(navController)*/
        }
    }
}




