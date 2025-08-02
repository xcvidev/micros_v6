package com.xcvi.micros.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.xcvi.micros.ui.screens.dashboard.DashboardViewModel
import kotlinx.serialization.Serializable

@Serializable
data object FoodDestination {

    const val ROUTE = "main/home"

    fun NavGraphBuilder.foodGraph(
        navController: NavHostController,
        dashboardViewModel: DashboardViewModel,
    ) {
        navigation(
            startDestination = ROUTE,
            route = Graphs.FOOD
        ){
            composable(route = ROUTE){  }
        }
    }
}












