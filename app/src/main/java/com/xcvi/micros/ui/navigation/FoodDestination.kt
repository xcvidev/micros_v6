package com.xcvi.micros.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.xcvi.micros.ui.core.comp.slidingComposable
import com.xcvi.micros.ui.navigation.destinations.GoalsDestination
import com.xcvi.micros.ui.navigation.destinations.MealDestination
import com.xcvi.micros.ui.navigation.destinations.SearchDestination
import com.xcvi.micros.ui.navigation.destinations.StatsDestination
import com.xcvi.micros.ui.screens.dashboard.DashboardScreen
import com.xcvi.micros.ui.screens.dashboard.DashboardViewModel
import com.xcvi.micros.ui.screens.goals.GoalsScreen
import com.xcvi.micros.ui.screens.goals.GoalsViewModel
import com.xcvi.micros.ui.screens.meal.MealScreen
import com.xcvi.micros.ui.screens.meal.MealViewModel
import com.xcvi.micros.ui.screens.search.SearchScreen
import com.xcvi.micros.ui.screens.search.SearchViewModel
import com.xcvi.micros.ui.screens.stats.StatsScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

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
        ) {
            composable(route = ROUTE) {
                DashboardScreen(
                    state = dashboardViewModel.state,
                    onEvent = dashboardViewModel::onEvent,
                    onGotMeal = { meal ->
                        navController.navigate(
                            MealDestination(
                                date = meal.date,
                                label = meal.name,
                                number = meal.number
                            )
                        )
                        if (meal.portions.isEmpty()) {
                            navController.navigate(
                                SearchDestination(
                                    mealNumber = meal.number,
                                    date = meal.date,
                                    mealLabel = meal.name
                                )
                            )
                        }
                    },
                    onGotoGoals = { navController.navigate(GoalsDestination) },
                    onGotoStats = { navController.navigate(StatsDestination) }
                )
            }

            slidingComposable<MealDestination> {
                val args = it.toRoute<MealDestination>()
                val viewModel = koinViewModel<MealViewModel>()
                MealScreen(
                    number = args.number,
                    date = args.date,
                    label = args.label,
                    state = viewModel.state,
                    onEvent = viewModel::onEvent,
                    onBack = { navController.popBackStack() },
                    onGotoAdd = {
                        navController.navigate(
                            SearchDestination(
                                date = args.date,
                                mealNumber = args.number,
                                mealLabel = args.label
                            )
                        )
                    }
                )
            }

            slidingComposable<SearchDestination> {
                val args = it.toRoute<SearchDestination>()
                val viewModel = koinViewModel<SearchViewModel>()
                SearchScreen(
                    date = args.date,
                    meal = args.mealNumber,
                    state = viewModel.state,
                    onEvent = viewModel::onEvent,
                    onBack = { navController.popBackStack() }
                )
            }

            slidingComposable<StatsDestination> {
                StatsScreen { navController.popBackStack() }
            }
            slidingComposable<GoalsDestination> {
                val viewModel = koinViewModel<GoalsViewModel>()
                GoalsScreen(
                    state = viewModel.state,
                    onEvent = viewModel::onEvent,
                    onAsk = {
                        navController.navigate(MessageDestination.ROUTE){
                            popUpTo(ROUTE){
                                inclusive = false
                            }
                        }
                    },
                    onBack = { navController.popBackStack() },
                    date = dashboardViewModel.state.currentDate
                )
            }
        }
    }
}












