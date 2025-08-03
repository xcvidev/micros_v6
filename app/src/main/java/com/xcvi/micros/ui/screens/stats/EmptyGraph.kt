package com.xcvi.micros.ui.screens.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.domain.utils.monthFormatted
import com.xcvi.micros.ui.core.comp.MultiLineDotGraph
import kotlinx.datetime.LocalDate
import kotlin.random.Random

@Composable
fun EmptyGraph(
    showMultiLines: Boolean = false,
    height: Dp,
    noDataText: String,
    onValueChange: (Int) -> Unit,
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        val data1 =  (1..12).map { Random.nextInt(150, 200).toDouble() }
        val data2 =  (1..12).map { Random.nextInt(0, 600).toDouble() }
        val data3 =  (1..12).map { Random.nextInt(20, 150).toDouble() }
        val xAxis = (1..12).map {
            LocalDate(
                2025,
                it,
                1
            ).monthFormatted(true)
        }
        val colors = listOf(
            MaterialTheme.colorScheme.onSurface.copy(0.1f),
            MaterialTheme.colorScheme.onSurface.copy(0.15f),
            MaterialTheme.colorScheme.onSurface.copy(0.2f),
        )
        MultiLineDotGraph(
            yAxes = if(showMultiLines) listOf(data1, data2, data3) else listOf(data1),
            xAxis = xAxis,
            height = height,
            lineColors = colors,
            dotColors = colors,
            dotSize = 2.dp,
            onValueChange = { index ->
                onValueChange(index)
            },
        )
        Text(text = noDataText, Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
