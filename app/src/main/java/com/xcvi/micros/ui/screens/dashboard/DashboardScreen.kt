package com.xcvi.micros.ui.screens.dashboard


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Meal
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.preferences.UserPreferences
import com.xcvi.micros.ui.core.comp.DateSelectorDialog
import com.xcvi.micros.ui.core.comp.HorizontalFadedBox
import com.xcvi.micros.ui.core.comp.OnNavigation
import com.xcvi.micros.ui.core.comp.rememberShakeOffset
import com.xcvi.micros.ui.screens.dashboard.comp.AnimatedMealCard
import com.xcvi.micros.ui.screens.dashboard.comp.DashboardDatePicker
import com.xcvi.micros.ui.screens.dashboard.comp.GoalsCard
import com.xcvi.micros.ui.screens.dashboard.comp.MealCardEmpty
import com.xcvi.micros.ui.screens.dashboard.comp.MealCardFull
import com.xcvi.micros.ui.screens.dashboard.comp.StatsCard
import com.xcvi.micros.ui.screens.dashboard.comp.summary_cards.DashboardSummary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
    onGotMeal: (Meal) -> Unit,
    onGotoStats: () -> Unit,
    onGotoGoals: () -> Unit,
    modifier: Modifier = Modifier,
) {

    OnNavigation {
        //onEvent(DashboardEvent.ChangeDate(getToday()))
    }

    val haptics = LocalHapticFeedback.current

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }


    var selectedMeal: Meal? by remember { mutableStateOf(null) }
    var copiedMeal: Meal? by remember { mutableStateOf(null) }

    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        DateSelectorDialog(
            currentDate = state.currentDate,
            onDismissRequest = { showDatePicker = false },
            onDateChanged = {
                onEvent(DashboardEvent.ChangeDate(date = it))
            },
            showFutureDates = true
        )
    }


    Scaffold(
        bottomBar = { BottomAppBar {} },
        modifier = modifier
            .fillMaxSize()
            .offset(x = shakeOffset),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.food_dashboard_topbar),
                        fontSize = 24.sp
                    )
                },
                actions = {
                    val today = getToday()
                    val color by animateColorAsState(
                        //if (state.currentDate !in today - 1..today + 1) {
                        if (state.currentDate != today) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            Color.Transparent
                        }
                    )
                    IconButton(
                        enabled = state.currentDate != today,
                        onClick = {
                            showDatePicker = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "",
                            tint = color
                        )
                    }

                }
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp)
        ) {
            HorizontalFadedBox(
                modifier = Modifier.padding(bottom = 12.dp),
                height = 40.dp,
                horizontalFade = 50.dp,
                targetColor = MaterialTheme.colorScheme.background
            ) {
                DashboardDatePicker(
                    centerValue = state.currentDate,
                    onCenterValueChange = {
                        onEvent(DashboardEvent.ChangeDate(date = it))
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    onCenterClick = {
                        showDatePicker = true
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    context = context,
                    height = 40.dp,
                    spacing = 130.dp,
                    fontSize = 20.sp,
                    sizeScaleDifference = 0.25f,
                )
            }
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {

                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = stringResource(R.string.summary),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                    )
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    DashboardSummary(
                        context = context,
                        nutrients = state.nutrients,
                        minerals = state.minerals,
                        vitamins = state.vitamins,
                        aminoAcids = state.aminoAcids,
                    )
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = stringResource(R.string.meals),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                    )
                }
                items(
                    items = state.meals,
                    key = { it.number }
                ) { mealCard ->
                    AnimatedMealCard(visible = mealCard.isVisible) {
                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    onClick = { onGotMeal(mealCard) },
                                    onLongClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        showEditDialog = true
                                        selectedMeal = mealCard
                                    }
                                )
                        ) {
                            MealCardFull(meal = mealCard)
                        }
                    }
                }

                if (state.meals.filter { it.isVisible }.size < 8) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .wrapContentSize()
                                .clickable { onEvent(DashboardEvent.AddMeal) }
                        ) {
                            MealCardEmpty()
                        }
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = stringResource(R.string.analytics_section_title).replace("-", "&"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, top = 36.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                    )
                }

                item {
                    StatsCard {
                        onGotoStats()
                    }
                }
                item {
                    GoalsCard(state.summary) {
                        onGotoGoals()
                    }
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(modifier = Modifier.height(60.dp))
                }

            }
        }

    }



    if (showEditDialog) {
        selectedMeal?.let { meal ->
            EditDialog(
                pasteEnabled = true,
                isCopying = copiedMeal == null,
                selectedMeal = meal,
                onDismiss = {
                    showDeleteDialog = false
                    showEditDialog = false
                },
                onClear = {
                    if (meal.portions.isEmpty()) {
                        showEditDialog = false
                        onEvent(DashboardEvent.DeleteMeal(meal) { shakeTrigger = true })
                        if (selectedMeal == copiedMeal) {
                            copiedMeal = null
                        }
                        selectedMeal = null
                    } else {
                        showDeleteDialog = true
                    }
                },
                onCopy = {
                    copiedMeal = meal
                    showEditDialog = false
                },
                onPaste = {
                    onEvent(
                        DashboardEvent.PasteMeal(
                            source = copiedMeal,
                            destination = meal,
                            onError = { shakeTrigger = true },
                            onSuccess = {
                                showEditDialog = false
                                selectedMeal = null
                                copiedMeal = null
                            }
                        )
                    )
                },
                onPin = {
                    UserPreferences.setMealFavorite(it.number, !it.isPinned)
                    showEditDialog = false
                }
            )
        }
    }

    if (showDeleteDialog) {
        selectedMeal?.let { meal ->
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    showEditDialog = false
                },
                title = {
                    Text(text = stringResource(R.string.clear))
                },
                text = { Text(stringResource(R.string.delete_confirm_short_text)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEvent(DashboardEvent.DeleteMeal(meal) { shakeTrigger = true })
                            showDeleteDialog = false
                            showEditDialog = false
                            if (selectedMeal == copiedMeal) {
                                copiedMeal = null
                            }
                            selectedMeal = null
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}


@Composable
fun EditDialog(
    pasteEnabled: Boolean,
    isCopying: Boolean,
    selectedMeal: Meal,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onPaste: () -> Unit,
    onCopy: () -> Unit,
    onPin: (Meal) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedMeal.name)
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = {
                        onPin(selectedMeal)
                    }
                ) {
                    Icon(
                        imageVector = if(selectedMeal.isPinned){
                            Icons.Default.PushPin
                        } else {
                            Icons.Outlined.PushPin
                        },
                        contentDescription = "",
                        modifier = Modifier.size(24.dp),
                        tint = if(selectedMeal.isPinned){
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        }
                    )
                }
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                val buttonText = if (selectedMeal.isPinned) {
                    stringResource(R.string.clear)
                } else {
                    stringResource(R.string.remove)
                }

                val enabled = if (selectedMeal.isPinned) {
                    selectedMeal.portions.isNotEmpty()
                } else {
                    true
                }

                Spacer(Modifier.weight(1f))
                TextButton(
                    enabled = enabled,
                    onClick = {
                        onClear()
                    }
                ) {
                    Text(text = buttonText)
                }
                Spacer(Modifier.width(8.dp))
                if (isCopying) {
                    TextButton(
                        enabled = selectedMeal.portions.isNotEmpty(),
                        onClick = onCopy
                    ) {
                        Text(text = stringResource(R.string.copy))
                    }
                } else {
                    TextButton(
                        enabled = pasteEnabled,
                        onClick = onPaste
                    ) {
                        Text(text = stringResource(R.string.paste))
                    }
                }
            }
        }
    )
}
