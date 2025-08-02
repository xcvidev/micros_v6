package com.xcvi.micros.ui.screens.goals


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Macros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalsSheet(
    goals: Macros,
    height: Dp,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    onConfirm: (Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    ModalBottomSheet(
        modifier = modifier.padding(horizontal = 8.dp),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        TabRowExample(
            goals = goals,
            height = height,
            onConfirm = onConfirm,
        )
    }
}


@Composable
fun TabRowExample(
    goals: Macros,
    height: Dp,
    onConfirm: (Int, Int, Int) -> Unit,
) {
    val tabTitles = listOf(
        stringResource(R.string.use_calories),
        stringResource(R.string.use_macros)
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.height(height)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
        ) {
            tabTitles.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier.clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
                ) {
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    )
                }
            }
        }

        when (selectedTabIndex) {
            0 -> ByCalories(goals = goals, onConfirm = onConfirm)
            1 -> ByMacros(goals = goals, onConfirm = onConfirm)
        }
    }
}