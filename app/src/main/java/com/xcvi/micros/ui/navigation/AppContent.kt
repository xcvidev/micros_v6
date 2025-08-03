package com.xcvi.micros.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xcvi.micros.R
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
    val messageViewModel: MessageViewModel = koinViewModel()    // scoped to activity

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
            messageGraph(navController, messageViewModel)
            weightGraph()
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                route = FoodDestination.ROUTE,
                navController = navController,
                selected = currentRoute == FoodDestination.ROUTE,
                icon = R.drawable.ic_dashboard
            )
            BottomBarItem(
                route = MessageDestination.ROUTE,
                navController = navController,
                selected = currentRoute == MessageDestination.ROUTE,
                icon = R.drawable.ic_search
            )
            BottomBarItem(
                route = WeightDestination.ROUTE,
                navController = navController,
                selected = currentRoute == WeightDestination.ROUTE,
                icon = R.drawable.ic_scale
            )
            /*
            BottomBarItem(
                route = SettingsDestination.ROUTE,
                navController = navController,
                selected = currentRoute == SettingsDestination.ROUTE,
                icon = R.drawable.ic_settings
            )
             */
        }
    }
}

@Composable
fun BottomBarItem(
    route: String,
    navController: NavHostController,
    selected: Boolean,
    icon: Int,
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.3f else 1f,
        animationSpec = spring()
    )


    IconButton(
        onClick = {
            if (!selected) {
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .size(24.dp),
            tint = iconColor
        )
    }
}




