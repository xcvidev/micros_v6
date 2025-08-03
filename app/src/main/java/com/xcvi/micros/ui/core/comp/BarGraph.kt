package com.xcvi.micros.ui.core.comp

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.domain.utils.roundToInt
import kotlinx.coroutines.launch
import kotlin.collections.iterator
import kotlin.math.abs


@Composable
fun BarGraph(
    yAxis: List<Int>,
    xAxis: List<String>,
    maxY: Double,
    onValueChange: (Int) -> Unit,
    tickSpacingDp: Dp = 70.dp,
    height: Dp,
    selectedTextColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedBarColor: Color = MaterialTheme.colorScheme.primary,
    barColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
    horizontalClickTolerancePx: Float = 200f,
    verticalClickTolerancePx: Float = 150f,
) {

    if(yAxis.isEmpty() || xAxis.isEmpty() || maxY <= 0.0) return
    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val valueRange = 0..(yAxis.lastIndex)
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx

    val scrollOffsetAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(yAxis, xAxis, maxY) {
        if (yAxis.isNotEmpty()) {
            val initialOffset = (yAxis.lastIndex) * tickSpacingPx
            scrollOffsetAnim.snapTo(initialOffset)
        }
    }

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt() + valueRange.first
    val clampedValue = centerValue.coerceIn(valueRange)
    onValueChange(clampedValue)

    // Snap to nearest bar after scroll ends
    LaunchedEffect(scrollState.isScrollInProgress) {
        if (!scrollState.isScrollInProgress) {
            val nearestTick = (scrollOffsetAnim.value / tickSpacingPx).roundToInt()
            val targetOffset = nearestTick * tickSpacingPx
            scrollOffsetAnim.animateTo(targetOffset)
        }
    }

    val labelBounds = remember { mutableStateMapOf<Int, Rect>() }

    Box(
        modifier = Modifier.pointerInput(Unit) {
                detectTapGestures { offset ->
                    val tapX = offset.x
                    val centerX = size.width / 2f
                    val relativeX = scrollOffsetAnim.value + (tapX - centerX)
                    val tappedIndex = (relativeX / tickSpacingPx).roundToInt()

                    if (tappedIndex in valueRange) {
                        val tickX =
                            centerX - scrollOffsetAnim.value + (tappedIndex - valueRange.first) * tickSpacingPx
                        val tapY = offset.y

                        if (
                            abs(tapX - tickX) <= horizontalClickTolerancePx &&
                            tapY in (size.height / 2 - verticalClickTolerancePx)..(size.height / 2 + verticalClickTolerancePx)
                        ) {
                            val targetOffset = (tappedIndex - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
                            return@detectTapGestures
                        }
                    }

                    // Handle label clicks
                    for ((index, bounds) in labelBounds) {
                        if (bounds.contains(offset)) {
                            val targetOffset = (index - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
                            break
                        }
                    }
                }
            }
            .scrollable(
                orientation = Orientation.Horizontal,
                state = scrollState,
                reverseDirection = false
            )
    ) {

        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value
            val barWidth = 12.dp.toPx()
            val maxBarHeight = size.height * 0.6f
            val bottomY = size.height * 0.75f // spacing between bars and labels

            labelBounds.clear()

            yAxis.forEachIndexed { i, yValue ->
                val x = startX + i * tickSpacingPx
                if (x < -barWidth || x > size.width + barWidth) return@forEachIndexed

                val heightRatio = (yValue.toFloat() / maxY.toFloat()).coerceIn(0f, 1f)
                val barHeight = heightRatio * maxBarHeight
                val isSelected = i == clampedValue
                val color = if (isSelected) selectedBarColor else barColor

                // Draw bar
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x - barWidth / 2, bottomY - barHeight),
                    size = Size(width = barWidth, height = barHeight),
                    cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                )

                val textColor = if (isSelected) selectedTextColor else unselectedTextColor
                // Draw x-axis label
                val label = xAxis.getOrNull(i) ?: ""
                val paint = Paint().apply {
                    this.color = textColor.toArgb()
                    textSize = 30f
                    textAlign = Paint.Align.CENTER
                }
                val labelY = bottomY + 80f

                val textWidth = paint.measureText(label)
                val fontMetrics = paint.fontMetrics

                labelBounds[i] = Rect(
                    left = x - textWidth / 2,
                    top = labelY - paint.textSize,
                    right = x + textWidth / 2,
                    bottom = labelY + 10f
                )

                if (isSelected) {
                    val bgPaddingX = 20f
                    val bgPaddingY = 12f
                    val rectLeft = x - textWidth / 2 - bgPaddingX
                    val rectTop = labelY + fontMetrics.ascent - bgPaddingY
                    val rectRight = x + textWidth / 2 + bgPaddingX
                    val rectBottom = labelY + fontMetrics.descent + bgPaddingY
                    drawRoundRect(
                        color = selectedBarColor.copy(alpha = 0.2f),
                        topLeft = Offset(rectLeft, rectTop),
                        size = Size(rectRight - rectLeft, rectBottom - rectTop),
                        cornerRadius = CornerRadius(40f, 40f)
                    )
                }

                drawContext.canvas.nativeCanvas.drawText(label, x, labelY, paint)

                // Approximate label bounds for tap detection
                val textWidthT = paint.measureText(label)
                val textHeight = paint.textSize

                labelBounds[i] = Rect(
                    left = x - textWidthT / 2,
                    top = labelY - textHeight,
                    right = x + textWidthT / 2,
                    bottom = labelY + 10f
                )
            }
        }
    }
}






/*
@Composable
fun DotGraph(
    yAxis: List<Double>,
    xAxis: List<String>,
    maxY: Double,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tickSpacingDp: Dp = 70.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
    selectedDotColor: Color = MaterialTheme.colorScheme.primary,
    unselectedDotColor: Color = selectedDotColor,
    selectedLabelColor: Color = MaterialTheme.colorScheme.primary,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
    lineColor: Color = MaterialTheme.colorScheme.primary,
    horizontalClickTolerancePx: Float = 200f,
    verticalClickTolerancePx: Float = 100f,
) {
    if(yAxis.isEmpty() || xAxis.isEmpty() || maxY <= 0.0) {
        return
    }

    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val valueRange = 0..(yAxis.lastIndex)
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx

    val scrollOffsetAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(yAxis, xAxis, maxY) {
        if (yAxis.isNotEmpty()) {
            val initialOffset = (yAxis.lastIndex) * tickSpacingPx
            scrollOffsetAnim.snapTo(initialOffset)
        }
    }

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt() + valueRange.first
    val clampedValue = centerValue.coerceIn(valueRange)
    onValueChange(clampedValue)

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (!scrollState.isScrollInProgress) {
            val nearestTick = (scrollOffsetAnim.value / tickSpacingPx).roundToInt()
            val targetOffset = nearestTick * tickSpacingPx
            scrollOffsetAnim.animateTo(targetOffset)
        }
    }

    val labelBounds = remember { mutableStateMapOf<Int, Rect>() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val tapX = offset.x
                    val centerX = size.width / 2f
                    val relativeX = scrollOffsetAnim.value + (tapX - centerX)
                    val tappedIndex = (relativeX / tickSpacingPx).roundToInt()

                    if (tappedIndex in valueRange) {
                        val tickX =
                            centerX - scrollOffsetAnim.value + (tappedIndex - valueRange.first) * tickSpacingPx
                        val tapY = offset.y

                        if (
                            abs(tapX - tickX) <= horizontalClickTolerancePx &&
                            tapY in (size.height / 2 - verticalClickTolerancePx)..(size.height / 2 + verticalClickTolerancePx)
                        ) {
                            val targetOffset = (tappedIndex - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
                            return@detectTapGestures
                        }
                    }

                    for ((index, bounds) in labelBounds) {
                        if (bounds.contains(offset)) {
                            val targetOffset = (index - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
                            break
                        }
                    }
                }
            }
            .scrollable(
                orientation = Orientation.Horizontal,
                state = scrollState,
                reverseDirection = false
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value
            val dotRadius = 3.dp.toPx() // Smaller dots
            val maxBarHeight = size.height * 0.6f
            val bottomY = size.height * 0.75f

            labelBounds.clear()

            val points = mutableListOf<Offset>()

            yAxis.forEachIndexed { i, yValue ->
                val x = startX + i * tickSpacingPx
                if (x < -dotRadius || x > size.width + dotRadius) return@forEachIndexed


                val heightRatio = (yValue / maxY).coerceIn(0.0, 1.0)
                val barHeight = (heightRatio * maxBarHeight).toFloat()
                //val y = bottomY - barHeight / 2f
                val y = bottomY - barHeight

                points += Offset(x, y)
            }

            // Draw line connecting all visible dots
            if (points.size >= 2) {
                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Draw dots and labels
            yAxis.forEachIndexed { i, calories ->
                val x = startX + i * tickSpacingPx
                if (x < -dotRadius || x > size.width + dotRadius) return@forEachIndexed

                val heightRatio = (calories / maxY).coerceIn(0.0, 1.0)
                val barHeight = (heightRatio * maxBarHeight).toFloat()
               // val y = bottomY - barHeight / 2f
                val y = bottomY - barHeight

                val isSelected = i == clampedValue
                val color = if (isSelected) selectedDotColor else unselectedDotColor

                drawCircle(
                    color = color,
                    radius = if(isSelected) dotRadius*1.2f else dotRadius,
                    center = Offset(x, y)
                )

                //Labels
                val label = xAxis.getOrNull(i) ?: ""
                val paint = android.graphics.Paint().apply {
                    this.color = if (isSelected) selectedLabelColor.toArgb() else unselectedLabelColor.toArgb()
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                val labelY = bottomY + 80f

                //drawContext.canvas.nativeCanvas.drawText(label, x, labelY, paint)

                val textWidth = paint.measureText(label)
                val textHeight = paint.textSize
                labelBounds[i] = Rect(
                    left = x - textWidth / 2,
                    top = labelY - textHeight,
                    right = x + textWidth / 2,
                    bottom = labelY + 10f
                )

                if (isSelected) {
                    val bgPaddingX = 20f
                    val bgPaddingY = 12f
                    val fontMetrics = paint.fontMetrics
                    val rectLeft = x - textWidth / 2 - bgPaddingX
                    val rectTop = labelY + fontMetrics.ascent - bgPaddingY
                    val rectRight = x + textWidth / 2 + bgPaddingX
                    val rectBottom = labelY + fontMetrics.descent + bgPaddingY
                    drawRoundRect(
                        color = selectedLabelColor.copy(alpha = 0.2f),
                        topLeft = Offset(rectLeft, rectTop),
                        size = Size(rectRight - rectLeft, rectBottom - rectTop),
                        cornerRadius = CornerRadius(40f, 40f)
                    )
                }

                // Draw text after background (so it appears on top)
                drawContext.canvas.nativeCanvas.drawText(label, x, labelY, paint)
            }
        }

    }
}


 */