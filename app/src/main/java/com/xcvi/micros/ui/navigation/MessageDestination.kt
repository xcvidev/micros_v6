package com.xcvi.micros.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.xcvi.micros.ui.screens.message.MessageScreen
import com.xcvi.micros.ui.screens.message.MessageViewModel
import kotlinx.serialization.Serializable

@Serializable
data object MessageDestination {
    const val ROUTE = "message/main"

    fun NavGraphBuilder.messageGraph(
        navController: NavHostController,
        assistantViewModel: MessageViewModel,
    ) {
        navigation(
            startDestination = ROUTE,
            route = Graphs.MESSAGE
        ) {
            composable(ROUTE) {
                MessageScreen(
                    state = assistantViewModel.state,
                    onEvent = assistantViewModel::onEvent,
                    onScan = {

                    },
                    onAdd = { barcode ->

                    }
                )
            }

        }
    }
}
