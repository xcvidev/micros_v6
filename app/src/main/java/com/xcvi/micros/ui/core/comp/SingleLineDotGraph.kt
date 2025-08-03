package com.xcvi.micros.ui.core.comp


import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun SingleLineDotGraph(
    yAxis: List<Double?>,
    xAxis: List<String>,
    onValueChange: (Int) -> Unit,
    height: Dp,
    tickSpacingDp: Dp = 70.dp,
    dotSize: Dp = 3.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
    selectedDotColor: Color = MaterialTheme.colorScheme.primary,
    unselectedDotColor: Color = selectedDotColor,
    selectedLabelColor: Color = MaterialTheme.colorScheme.primary,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    lineColor: Color = MaterialTheme.colorScheme.primary,
    horizontalClickTolerancePx: Float = 200f,
    verticalClickTolerancePx: Float = 100f,
) {
    if (yAxis.isEmpty() || xAxis.isEmpty()) return

    val maxY = yAxis.filterNotNull().maxOrNull() ?: 1.0

    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val valueRange = 0..(yAxis.lastIndex)
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx

    val scrollOffsetAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(yAxis, xAxis, maxY) {
        val initialOffset = (yAxis.lastIndex) * tickSpacingPx
        scrollOffsetAnim.snapTo(initialOffset)
    }

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt()
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
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
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
                            scope.launch { scrollOffsetAnim.animateTo(targetOffset) }
                            return@detectTapGestures
                        }
                    }

                    for ((index, bounds) in labelBounds) {
                        if (bounds.contains(offset)) {
                            val targetOffset = (index - valueRange.first) * tickSpacingPx
                            scope.launch { scrollOffsetAnim.animateTo(targetOffset) }
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
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value
            val dotRadius = dotSize.toPx()
            val maxBarHeight = size.height * 0.6f
            val bottomY = size.height * 0.75f

            labelBounds.clear()

            //  Normalize using min/max of non-null values
            val nonNullValues = yAxis.filterNotNull()
            val normMin = nonNullValues.minOrNull() ?: 0.0
            val normMax = nonNullValues.maxOrNull() ?: 1.0
            val rangeY = (normMax - normMin).takeIf { it != 0.0 } ?: 1.0

            val baselineOffsetFraction = 0.33f
            val baselineOffset = maxBarHeight * baselineOffsetFraction
            val usableHeight = maxBarHeight - baselineOffset

            val points = buildList {
                yAxis.forEachIndexed { i, yValue ->
                    if (yValue != null) {
                        val x = startX + i * tickSpacingPx
                        val heightRatio = ((yValue - normMin) / rangeY).coerceIn(0.0, 1.0)
                        val barHeight = (heightRatio * usableHeight).toFloat()
                        val y = bottomY - baselineOffset - barHeight / 1.5f
                        add(Offset(x, y))
                    }
                }
            }


            // Draw line connecting all non-null points
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
            yAxis.forEachIndexed { i, yValue ->

                val x = startX + i * tickSpacingPx
                if (x < -dotRadius || x > size.width + dotRadius) return@forEachIndexed

                val label = xAxis.getOrNull(i) ?: ""
                val isSelected = i == clampedValue

                val paint = Paint().apply {
                    color =
                        if (isSelected) selectedLabelColor.toArgb() else unselectedLabelColor.toArgb()
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
                        color = selectedLabelColor.copy(alpha = 0.2f),
                        topLeft = Offset(rectLeft, rectTop),
                        size = Size(rectRight - rectLeft, rectBottom - rectTop),
                        cornerRadius = CornerRadius(40f, 40f)
                    )
                }

                drawContext.canvas.nativeCanvas.drawText(label, x, labelY, paint)

                yValue?.let { value ->
                    val heightRatio = ((value - normMin) / rangeY).coerceIn(0.0, 1.0)
                    val barHeight = (heightRatio * usableHeight).toFloat()
                    val y = bottomY - baselineOffset - barHeight / 1.5f


                    val color = if (isSelected) selectedDotColor else unselectedDotColor
                    drawCircle(
                        color = color,
                        radius = if (isSelected) dotRadius * 1.2f else dotRadius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}


/**Normalized**/
/*
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value
            val dotRadius = dotSize.toPx()
            val maxBarHeight = size.height * 0.6f
            val bottomY = size.height * 0.75f

            labelBounds.clear()

            //  Normalize using min/max of non-null values
            val nonNullValues = yAxis.filterNotNull()
            val normMin = nonNullValues.minOrNull() ?: 0.0
            val normMax = nonNullValues.maxOrNull() ?: 1.0
            val rangeY = (normMax - normMin).takeIf { it != 0.0 } ?: 1.0

            val baselineOffsetFraction = 0.33f
            val baselineOffset = maxBarHeight * baselineOffsetFraction
            val usableHeight = maxBarHeight - baselineOffset

            val points = buildList {
                yAxis.forEachIndexed { i, yValue ->
                    if (yValue != null) {
                        val x = startX + i * tickSpacingPx
                        val heightRatio = ((yValue - normMin) / rangeY).coerceIn(0.0, 1.0)
                        val barHeight = (heightRatio * usableHeight).toFloat()
                        val y = bottomY - baselineOffset - barHeight / 1.5f
                        add(Offset(x, y))
                    }
                }
            }


            // Draw line connecting all non-null points
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
            yAxis.forEachIndexed { i, yValue ->

                val x = startX + i * tickSpacingPx
                if (x < -dotRadius || x > size.width + dotRadius) return@forEachIndexed

                val label = xAxis.getOrNull(i) ?: ""
                val isSelected = i == clampedValue

                val paint = android.graphics.Paint().apply {
                    color = if (isSelected) selectedLabelColor.toArgb() else unselectedLabelColor.toArgb()
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
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
                        color = selectedLabelColor.copy(alpha = 0.2f),
                        topLeft = Offset(rectLeft, rectTop),
                        size = Size(rectRight - rectLeft, rectBottom - rectTop),
                        cornerRadius = CornerRadius(40f, 40f)
                    )
                }

                drawContext.canvas.nativeCanvas.drawText(label, x, labelY, paint)

                yValue?.let { value ->
                    val heightRatio = ((value - normMin) / rangeY).coerceIn(0.0, 1.0)
                    val barHeight = (heightRatio * usableHeight).toFloat()
                    val y = bottomY - baselineOffset - barHeight / 1.5f


                    val color = if (isSelected) selectedDotColor else unselectedDotColor
                    drawCircle(
                        color = color,
                        radius = if (isSelected) dotRadius * 1.2f else dotRadius,
                        center = Offset(x, y)
                    )
                }
            }
        }

 */

/*
@Composable
fun DotGraph(
    yAxis: List<Double?>,
    xAxis: List<String>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tickSpacingDp: Dp = 70.dp,
    dotSize: Dp = 3.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
    selectedDotColor: Color = MaterialTheme.colorScheme.primary,
    unselectedDotColor: Color = selectedDotColor,
    selectedLabelColor: Color = MaterialTheme.colorScheme.primary,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    lineColor: Color = MaterialTheme.colorScheme.primary,
    horizontalClickTolerancePx: Float = 200f,
    verticalClickTolerancePx: Float = 100f,
) {
    if (yAxis.isEmpty() || xAxis.isEmpty()) return

    val maxY = yAxis.filterNotNull().maxOrNull() ?: 1.0

    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val valueRange = 0..(yAxis.lastIndex)
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx

    val scrollOffsetAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(yAxis, xAxis, maxY) {
        val initialOffset = (yAxis.lastIndex) * tickSpacingPx
        scrollOffsetAnim.snapTo(initialOffset)
    }

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt()
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
                            scope.launch { scrollOffsetAnim.animateTo(targetOffset) }
                            return@detectTapGestures
                        }
                    }

                    for ((index, bounds) in labelBounds) {
                        if (bounds.contains(offset)) {
                            val targetOffset = (index - valueRange.first) * tickSpacingPx
                            scope.launch { scrollOffsetAnim.animateTo(targetOffset) }
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
            val dotRadius = dotSize.toPx()
            val maxBarHeight = size.height * 0.6f
            val bottomY = size.height * 0.75f

            labelBounds.clear()

            val points = buildList {
                yAxis.forEachIndexed { i, yValue ->
                    if (yValue != null) {
                        val x = startX + i * tickSpacingPx
                        val heightRatio = (yValue / maxY).coerceIn(0.0, 1.0)
                        val barHeight = (heightRatio * maxBarHeight).toFloat()
                        val y = bottomY - barHeight/1.5f
                        add(Offset(x, y))
                    }
                }
            }

            // Draw line connecting all non-null points, even if some are off-screen
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
            yAxis.forEachIndexed { i, yValue ->
                val x = startX + i * tickSpacingPx
                if (x < -dotRadius || x > size.width + dotRadius) return@forEachIndexed

                val label = xAxis.getOrNull(i) ?: ""
                val isSelected = i == clampedValue

                val paint = android.graphics.Paint().apply {
                    color = if (isSelected) selectedLabelColor.toArgb() else unselectedLabelColor.toArgb()
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
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
                        color = selectedLabelColor.copy(alpha = 0.2f),
                        topLeft = Offset(rectLeft, rectTop),
                        size = Size(rectRight - rectLeft, rectBottom - rectTop),
                        cornerRadius = CornerRadius(40f, 40f)
                    )
                }

                drawContext.canvas.nativeCanvas.drawText(label, x, labelY, paint)

                // Only draw dots for non-null values
                yValue?.let { value ->
                    val heightRatio = (value / maxY).coerceIn(0.0, 1.0)
                    val barHeight = (heightRatio * maxBarHeight).toFloat()
                    val y = bottomY - barHeight /1.5f

                    val color = if (isSelected) selectedDotColor else unselectedDotColor
                    drawCircle(
                        color = color,
                        radius = if (isSelected) dotRadius * 1.2f else dotRadius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}
*/