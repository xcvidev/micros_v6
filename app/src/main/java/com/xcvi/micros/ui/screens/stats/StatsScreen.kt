package com.xcvi.micros.ui.screens.stats

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.utils.FilterType
import com.xcvi.micros.domain.model.weight.WeightSummary
import com.xcvi.micros.domain.utils.getLocalDate
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.ui.core.comp.DropDownChip
import com.xcvi.micros.ui.core.comp.OnNavigation
import com.xcvi.micros.ui.screens.dashboard.comp.FoodSummaryCard
import com.xcvi.micros.ui.screens.stats.comp.FoodGraph
import com.xcvi.micros.ui.screens.stats.comp.WeightGraph
import com.xcvi.micros.ui.screens.stats.comp.WeightSummaryText
import org.koin.androidx.compose.koinViewModel


fun FilterType.displayName(context: Context): String {
    return when (this) {
        FilterType.WEEK -> context.getString(R.string.stats_filter1)
        FilterType.MONTH -> context.getString(R.string.stats_filter2)
        FilterType.DAY -> context.getString(R.string.stats_filter3)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    statsViewModel: StatsViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state = statsViewModel.state
    val onEvent = statsViewModel::onEvent

    val context = LocalContext.current

    var selectedYear by remember {
        mutableIntStateOf(
            state.years.firstOrNull() ?: getToday().getLocalDate().year
        )
    }

    OnNavigation {
        onEvent(StatsEvent.ChangeYear(selectedYear))
    }
    val noFoodDataText: String = stringResource(R.string.stats_no_food_data)
    val noWeightDataText: String = stringResource(R.string.stats_no_weight_data)
    val maxLabel: String = stringResource(R.string.max)
    val minLabel: String = stringResource(R.string.min)
    val avgLabel: String = stringResource(R.string.avg)

    Scaffold(
        bottomBar = { Box(Modifier
            .height(48.dp)
            .width(24.dp)) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (state.years.size > 1) {
                        DropDownChip(
                            options = state.years,
                            selectedOption = selectedYear,
                            onOptionSelected = {
                                selectedYear = it
                                onEvent(StatsEvent.ChangeYear(it))
                            },
                            imageVector = Icons.Default.ArrowDropDown
                        )
                    }
                    DropDownChip(
                        options = listOf(FilterType.DAY, FilterType.WEEK, FilterType.MONTH),
                        selectedOption = state.filter,
                        onOptionSelected = {
                            onEvent(StatsEvent.ChangeFilter(it))
                        },
                        toString = { it.displayName(context) },
                        imageVector = Icons.AutoMirrored.Filled.Sort
                    )
                }
            )
        },
    ) { padding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            var foods by remember { mutableStateOf(MacrosSummary.empty()) }
            var weights by remember { mutableStateOf(WeightSummary.empty()) }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(R.string.food),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                )
                BoxWithConstraints(
                    modifier = Modifier.weight(1f)
                ) {
                    FoodGraph(
                        height = maxHeight,
                        calories = when (state.filter) {
                            FilterType.DAY -> state.caloriesByDay
                            FilterType.WEEK -> state.calorieByWeek
                            FilterType.MONTH -> state.caloriesByMonth
                        },
                        macros = when (state.filter) {
                            FilterType.DAY -> state.macrosByDay
                            FilterType.WEEK -> state.macrosByWeek
                            FilterType.MONTH -> state.macrosByMonth
                        },
                        showDate = state.filter != FilterType.MONTH,
                        noDataText = noFoodDataText,
                        onScroll = {
                            foods = it
                        }
                    )
                }

                FoodSummaryCard(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    summary = foods.actual,
                    backgroundColor = Color.Transparent,
                )
            }


            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.weight),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                )
                BoxWithConstraints(
                    modifier = Modifier.weight(1f)
                ) {
                    val maxHeight = maxHeight
                    WeightGraph(
                        height = maxHeight,
                        data = when (state.filter) {
                            FilterType.DAY -> state.weightsByDay
                            FilterType.WEEK -> state.weightsByWeek
                            FilterType.MONTH -> state.weightsByMonth
                        },
                        noDataText = noWeightDataText,
                        onScroll = {
                            weights = it
                        }
                    )
                }

                WeightSummaryText(
                    list = weights,
                    maxLabel = maxLabel,
                    minLabel = minLabel,
                    avgLabel = avgLabel,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}


