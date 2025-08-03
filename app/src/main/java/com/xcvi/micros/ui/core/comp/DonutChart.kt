package com.xcvi.micros.ui.core.comp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DonutChart(
    proportions: List<Float>, // Must sum to 1.0f (or close)
    colors: List<Color>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 100.dp,
    strokeWidth: Float = 40f, // Thickness of the donut
) {
    Canvas(modifier = modifier.size(chartSize)) {
        val diameter = size.minDimension
        val radius = diameter / 2
        val center = Offset(size.width / 2, size.height / 2)

        var startAngle = -90f // Start from top

        proportions.forEachIndexed { index, proportion ->
            val sweepAngle = proportion * 360f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                size = Size(diameter, diameter),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
            startAngle += sweepAngle
        }
    }
}