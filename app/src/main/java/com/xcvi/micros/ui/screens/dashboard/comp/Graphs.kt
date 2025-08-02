package com.xcvi.micros.ui.screens.dashboard.comp

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun MultiSmoothLineGraph(
    series: List<List<Double>>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Color.Blue, Color.Green, Color.Red),
    pointColors: List<Color> = colors,
    strokeWidth: Dp = 2.dp,
    drawPoints: Boolean = false
) {
    // Basic guards
    if (series.isEmpty() || series.any { it.isEmpty() }) return
    val pointCount = series.first().size
    if (series.any { it.size != pointCount }) return // require same X count

    // Global min/max across all series
    val allValues = series.flatten()
    val maxY = allValues.maxOrNull() ?: 1.0
    val minY = allValues.minOrNull() ?: 0.0
    val yRange = maxY - minY

    Canvas(modifier = modifier) {
        val spacing = size.width / (pointCount - 1).coerceAtLeast(1)
        val heightPx = size.height

        series.forEachIndexed { sIdx, values ->
            val lineColor = colors.getOrNull(sIdx) ?: Color.Black
            val pointColor = pointColors.getOrNull(sIdx) ?: lineColor

            // Map values -> Offsets; handle flat case
            val points = values.mapIndexed { i, v ->
                val x = i * spacing
                val y = if (yRange == 0.0) {
                    // All values identical across *all* series.
                    // Draw a visible middle line; change to 0f or heightPx if preferred.
                    heightPx / 2f
                } else {
                    val ratio = ((v - minY) / yRange).toFloat()
                    heightPx - (ratio * heightPx)
                }
                Offset(x, y)
            }

            // Build smoothed path (simple midpoint-based cubic)
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val midX = (prev.x + curr.x) / 2f
                    cubicTo(
                        midX, prev.y,
                        midX, curr.y,
                        curr.x, curr.y
                    )
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            if (drawPoints) {
                points.forEach { p ->
                    drawCircle(
                        color = pointColor,
                        radius = 4.dp.toPx(),
                        center = p
                    )
                }
            }
        }
    }
}


@Composable
fun MultiLineGraph(
    series: List<List<Double>>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp,
    drawPoints: Boolean = false
) {
    if (series.isEmpty() || colors.isEmpty()) return
    val pointCount = series.first().size
    if (series.any { it.size != pointCount }) return // Ensure all series have same length

    val allValues = series.flatten()
    val maxY = allValues.maxOrNull() ?: 1.0
    val minY = allValues.minOrNull() ?: 0.0
    val yRange = maxY - minY

    Canvas(modifier = modifier) {
        val spacing = size.width / (pointCount - 1).coerceAtLeast(1)
        val height = size.height

        series.forEachIndexed { seriesIndex, values ->
            val color = colors.getOrNull(seriesIndex) ?: Color.Black

            val points = values.mapIndexed { index, value ->
                val x = index * spacing
                val normalizedY = (value - minY) / yRange
                val y = height - (normalizedY * height).toFloat()
                Offset(x, y)
            }

            val path = Path().apply {
                moveTo(points.first().x, points.first().y)

                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val current = points[i]
                    val midX = (prev.x + current.x) / 2

                    cubicTo(
                        x1 = midX, y1 = prev.y,
                        x2 = midX, y2 = current.y,
                        x3 = current.x, y3 = current.y
                    )
                }
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            if (drawPoints) {
                points.forEach {
                    drawCircle(
                        color = color,
                        radius = 3.dp.toPx(),
                        center = it
                    )
                }
            }
        }
    }
}


@Composable
fun LineGraph(
    values: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    pointColor: Color = Color.Red,
    strokeWidth: Dp = 1.dp,
    drawPoints: Boolean = false
) {
    if (values.size < 2) return

    val maxY = values.maxOrNull() ?: 1.0
    val minY = values.minOrNull() ?: 0.0
    val yRange = maxY - minY
    val pointCount = values.size

    Canvas(modifier = modifier) {
        val spacing = size.width / (pointCount - 1).coerceAtLeast(1)
        val height = size.height

        val points = values.mapIndexed { index, value ->
            val x = index * spacing
            val normalizedY = (value - minY) / yRange
            val y = height - (normalizedY * height).toFloat()
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val current = points[i]
                val midX = (prev.x + current.x) / 2

                // Cubic Bezier: control point between previous and current
                cubicTo(
                    x1 = midX, y1 = prev.y,
                    x2 = midX, y2 = current.y,
                    x3 = current.x, y3 = current.y
                )
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        if (drawPoints) {
            points.forEach {
                drawCircle(
                    color = pointColor,
                    radius = 4.dp.toPx(),
                    center = it
                )
            }
        }
    }
}