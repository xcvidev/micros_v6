package com.xcvi.micros.ui.screens.stats.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.utils.getLocalDate
import com.xcvi.micros.domain.utils.monthFormatted
import com.xcvi.micros.ui.core.comp.MultiLineDotGraph
import com.xcvi.micros.ui.screens.stats.EmptyGraph
import com.xcvi.micros.ui.theme.carbsDark
import com.xcvi.micros.ui.theme.carbsLight
import com.xcvi.micros.ui.theme.fatsDark
import com.xcvi.micros.ui.theme.fatsLight
import com.xcvi.micros.ui.theme.proteinDark
import com.xcvi.micros.ui.theme.proteinLight

@Composable
fun FoodGraph(
    height: Dp,
    showDate: Boolean,
    macros: List<MacrosSummary>,
    calories: List<Double?>,
    noDataText: String,
    onScroll: (MacrosSummary) -> Unit,
) {

    if (macros.size < 2) {
        EmptyGraph(
            showMultiLines = true,
            height = height,
            noDataText = noDataText,
        ) { }
    } else {

        var selectedIndex by remember { mutableIntStateOf(0) }
        var selectedGraph by remember { mutableIntStateOf(0) }

        val labels = if (showDate) {
            macros.map {
                it.date.getLocalDate()
                    .monthFormatted(true) + " " + it.date.getLocalDate().dayOfMonth
            }
        } else {
            macros.map { it.date.getLocalDate().monthFormatted(true).uppercase() }
        }


        val isDark = isSystemInDarkTheme()

        val colors = if (isDark) {
            listOf(
                proteinDark,
                carbsDark,
                fatsDark
            )
        } else {
            listOf(
                proteinLight,
                carbsLight,
                fatsLight
            )
        }

        val currentValue = macros.getOrNull(selectedIndex) ?: MacrosSummary.empty()
        onScroll(currentValue)

        Box {
            if (selectedGraph == 0) {
                MacroGraph(
                    height = height,
                    macros = macros.map { it.actual },
                    onValueChange = { index ->
                        selectedIndex = index
                    },
                    xAxis = labels,
                    lineColors = colors,
                    dotColors = colors
                )
            } else {
                MultiLineDotGraph(
                    height = height,
                    yAxes = listOf(calories),
                    onValueChange = { index ->
                        selectedIndex = index
                    },
                    xAxis = labels,
                    lineColors = listOf(MaterialTheme.colorScheme.primary),
                    dotColors = listOf(MaterialTheme.colorScheme.primary)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        selectedGraph = (selectedGraph + 1) % 2
                    }
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))

            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedGraph == 0) stringResource(R.string.calories) else stringResource(R.string.macros),
                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (selectedGraph == 0) Icons.AutoMirrored.Filled.ShowChart else Icons.Default.StackedLineChart ,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

}


