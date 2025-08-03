package com.xcvi.micros.ui.screens.stats.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.domain.model.weight.WeightSummary
import com.xcvi.micros.domain.utils.roundDecimals
import com.xcvi.micros.ui.core.comp.MultiLineDotGraph
import com.xcvi.micros.ui.screens.stats.EmptyGraph
import com.xcvi.micros.ui.screens.weight.SummaryItem

@Composable
fun WeightGraph(
    height: Dp,
    onScroll: (WeightSummary) -> Unit,
    data: List<WeightSummary>,
    noDataText: String,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    if (data.size < 2) {
        EmptyGraph(
            noDataText = noDataText,
            height = height
        ) {

        }
    }
    else {
        val yAxis = data.map {
            if(it.avg > 0) it.avg else null
        }

        MultiLineDotGraph(
            yAxes = listOf(yAxis),
            xAxis = data.map { it.label },
            onValueChange = { index ->
                selectedIndex = index
            },
            height = height,
            lineColors = listOf(MaterialTheme.colorScheme.primary)
        )
        val currentWeights = data[selectedIndex]
        onScroll(currentWeights)
    }
}


@Composable
fun WeightSummaryText(
    modifier: Modifier = Modifier,
    list: WeightSummary,
    maxLabel: String,
    minLabel: String,
    avgLabel: String
) {

    val min = list.min
    val max = list.max
    val avg = list.avg
    val unit = if(min == 0.0 && max == 0.0 && avg == 0.0){"" } else {
        list.unit.name
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            SummaryItem(label = minLabel, value = min.roundDecimals().toString(), unit)
            SummaryItem(label = avgLabel, value = avg.roundDecimals().toString(), unit)
            SummaryItem(label = maxLabel, value = max.roundDecimals().toString(), unit)
        }
    }
}

