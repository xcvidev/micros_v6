package com.xcvi.micros.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.xcvi.micros.ui.screens.weight.WeightScreen
import com.xcvi.micros.ui.screens.weight.WeightViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object WeightDestination {
    const val ROUTE = "weight/main"
    fun NavGraphBuilder.weightGraph(/*navController: NavHostController*/) {
        navigation(
            startDestination = WeightDestination.ROUTE,
            route = Graphs.WEIGHT
        ) {
            composable(WeightDestination.ROUTE) {
                val viewModel: WeightViewModel = koinViewModel()
                WeightScreen(state = viewModel.state, onEvent = viewModel::onEvent)
            }
        }
    }
}
