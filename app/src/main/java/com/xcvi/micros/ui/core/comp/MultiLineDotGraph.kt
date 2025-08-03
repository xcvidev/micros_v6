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

/*
@Composable
fun MacroGraph(
    macros: List<Macros>,
    xAxis: List<String>,
    onValueChange: (Int) -> Unit,
    height: Dp,
    lineColors: List<Color>,
    dotColors: List<Color> = lineColors,
    tickSpacingDp: Dp = 70.dp,
    dotSize: Dp = 3.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
    selectedLabelColor: Color = MaterialTheme.colorScheme.primary,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    horizontalClickTolerancePx: Float = 200f,
    verticalClickTolerancePx: Float = 100f,
) {
    if (macros.isEmpty() || macros.size != xAxis.size) return
    val yAxes = listOf(
        macros.map { if(it.protein > 0) it.protein.toDouble() else null  },
        macros.map { if(it.carbohydrates > 0) it.carbohydrates.toDouble() else null  },
        macros.map { if(it.fats > 0) it.fats.toDouble() else null  }
    )


    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val valueRange = 0..(xAxis.lastIndex)
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx

    val scrollOffsetAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(yAxes, xAxis) {
        val initialOffset = (xAxis.lastIndex) * tickSpacingPx
        scrollOffsetAnim.snapTo(initialOffset)
    }

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt().coerceIn(valueRange)
    onValueChange(centerValue)

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
                        val tickX = centerX - scrollOffsetAnim.value + tappedIndex * tickSpacingPx
                        val tapY = offset.y

                        if (
                            abs(tapX - tickX) <= horizontalClickTolerancePx &&
                            tapY in (size.height / 2 - verticalClickTolerancePx)..(size.height / 2 + verticalClickTolerancePx)
                        ) {
                            val targetOffset = tappedIndex * tickSpacingPx
                            scope.launch { scrollOffsetAnim.animateTo(targetOffset) }
                            return@detectTapGestures
                        }
                    }

                    for ((index, bounds) in labelBounds) {
                        if (bounds.contains(offset)) {
                            val targetOffset = index * tickSpacingPx
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
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value
            val dotRadius = dotSize.toPx()
            val maxBarHeight = size.height * 0.6f
            val bottomY = size.height * 0.75f

            labelBounds.clear()

            // Flatten and normalize all values across all lines
            val allNonNullValues = yAxes.flatten().filterNotNull()
            val normMin = allNonNullValues.minOrNull() ?: 0.0
            val normMax = allNonNullValues.maxOrNull() ?: 1.0
            val rangeY = (normMax - normMin).takeIf { it != 0.0 } ?: 1.0

            val baselineOffsetFraction = 0.33f
            val baselineOffset = maxBarHeight * baselineOffsetFraction
            val usableHeight = maxBarHeight - baselineOffset

            // Function to create a Catmull-Rom path from points
            fun createSmoothPath(points: List<Offset>): Path {
                val path = Path()
                if (points.size < 2) return path

                val n = points.size
                val x = points.map { it.x }
                val y = points.map { it.y }

                val dx = FloatArray(n - 1) { i -> x[i + 1] - x[i] }
                val dy = FloatArray(n - 1) { i -> y[i + 1] - y[i] }
                val m = FloatArray(n - 1) { i -> dy[i] / dx[i] }

                val slope = FloatArray(n)

                // Set first slope
                slope[0] = m[0]

                // Internal slopes
                for (i in 1 until n - 1) {
                    if (m[i - 1] * m[i] <= 0f) {
                        slope[i] = 0f
                    } else {
                        val w1 = dx[i - 1] + dx[i]
                        slope[i] = (3 * w1) / ((w1 + dx[i]) / m[i - 1] + (w1 + dx[i - 1]) / m[i])
                    }
                }

                // Last slope
                slope[n - 1] = m.last()

                // Start path
                path.moveTo(x[0], y[0])
                for (i in 0 until n - 1) {
                    val x0 = x[i]
                    val x1 = x[i + 1]
                    val y0 = y[i]
                    val y1 = y[i + 1]
                    val dx_i = x1 - x0

                    path.cubicTo(
                        x0 + dx_i / 3f, y0 + slope[i] * dx_i / 3f,
                        x1 - dx_i / 3f, y1 - slope[i + 1] * dx_i / 3f,
                        x1, y1
                    )
                }

                return path
            }

            // Draw lines and dots for each yAxis line
            yAxes.forEachIndexed { lineIndex, yAxis ->
                val lineColor = lineColors.getOrElse(lineIndex) { Color.Black }
                val dotColor = dotColors.getOrElse(lineIndex) { Color.Black }

                // Keep original index with each point
                val indexedPoints = yAxis.mapIndexedNotNull { i, yValue ->
                    yValue?.let {
                        val x = startX + i * tickSpacingPx
                        val heightRatio = ((it - normMin) / rangeY).coerceIn(0.0, 1.0)
                        val barHeight = (heightRatio * usableHeight).toFloat()
                        val y = bottomY - baselineOffset - barHeight / 1.5f
                        i to Offset(x, y)
                    }
                }

                val points = indexedPoints.map { it.second }

                if (points.size >= 2) {
                    val path = createSmoothPath(points)
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Draw dots with correct selection logic
                indexedPoints.forEach { (originalIndex, point) ->
                    val isSelected = originalIndex == centerValue
                    drawCircle(
                        color = dotColor,
                        radius = if (isSelected) dotRadius * 1.2f else dotRadius,
                        center = point
                    )
                }
            }


            // Draw labels for xAxis
            xAxis.forEachIndexed { i, label ->
                val x = startX + i * tickSpacingPx
                if (x < -dotRadius || x > size.width + dotRadius) return@forEachIndexed

                val isSelected = i == centerValue

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
            }
        }

    }
}
*/
@Composable
fun MultiLineDotGraph(
    yAxes: List<List<Double?>>,
    xAxis: List<String>,
    onValueChange: (Int) -> Unit,
    height: Dp,
    lineColors: List<Color>,
    dotColors: List<Color> = lineColors,
    tickSpacingDp: Dp = 70.dp,
    dotSize: Dp = 3.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
    selectedLabelColor: Color = MaterialTheme.colorScheme.primary,
    unselectedLabelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    horizontalClickTolerancePx: Float = 200f,
    verticalClickTolerancePx: Float = 100f,
) {
    if (yAxes.isEmpty() || yAxes.any { it.size != xAxis.size }) return

    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val valueRange = 0..(xAxis.lastIndex)
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx

    val scrollOffsetAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(yAxes, xAxis) {
        val initialOffset = (xAxis.lastIndex) * tickSpacingPx
        scrollOffsetAnim.snapTo(initialOffset)
    }

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt().coerceIn(valueRange)
    onValueChange(centerValue)

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
                        val tickX = centerX - scrollOffsetAnim.value + tappedIndex * tickSpacingPx
                        val tapY = offset.y

                        if (
                            abs(tapX - tickX) <= horizontalClickTolerancePx &&
                            tapY in (size.height / 2 - verticalClickTolerancePx)..(size.height / 2 + verticalClickTolerancePx)
                        ) {
                            val targetOffset = tappedIndex * tickSpacingPx
                            scope.launch { scrollOffsetAnim.animateTo(targetOffset) }
                            return@detectTapGestures
                        }
                    }

                    for ((index, bounds) in labelBounds) {
                        if (bounds.contains(offset)) {
                            val targetOffset = index * tickSpacingPx
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
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value
            val dotRadius = dotSize.toPx()
            val maxBarHeight = size.height * 0.6f
            val bottomY = size.height * 0.75f

            labelBounds.clear()

            // Flatten and normalize all values across all lines
            val allNonNullValues = yAxes.flatten().filterNotNull()
            val normMin = allNonNullValues.minOrNull() ?: 0.0
            val normMax = allNonNullValues.maxOrNull() ?: 1.0
            val rangeY = (normMax - normMin).takeIf { it != 0.0 } ?: 1.0

            val baselineOffsetFraction = 0.33f
            val baselineOffset = maxBarHeight * baselineOffsetFraction
            val usableHeight = maxBarHeight - baselineOffset

            // Function to create a Catmull-Rom path from points
            fun createSmoothPath(points: List<Offset>): Path {
                val path = Path()
                if (points.size < 2) return path

                val n = points.size
                val x = points.map { it.x }
                val y = points.map { it.y }

                val dx = FloatArray(n - 1) { i -> x[i + 1] - x[i] }
                val dy = FloatArray(n - 1) { i -> y[i + 1] - y[i] }
                val m = FloatArray(n - 1) { i -> dy[i] / dx[i] }

                val slope = FloatArray(n)

                // Set first slope
                slope[0] = m[0]

                // Internal slopes
                for (i in 1 until n - 1) {
                    if (m[i - 1] * m[i] <= 0f) {
                        slope[i] = 0f
                    } else {
                        val w1 = dx[i - 1] + dx[i]
                        slope[i] = (3 * w1) / ((w1 + dx[i]) / m[i - 1] + (w1 + dx[i - 1]) / m[i])
                    }
                }

                // Last slope
                slope[n - 1] = m.last()

                // Start path
                path.moveTo(x[0], y[0])
                for (i in 0 until n - 1) {
                    val x0 = x[i]
                    val x1 = x[i + 1]
                    val y0 = y[i]
                    val y1 = y[i + 1]
                    val dx_i = x1 - x0

                    path.cubicTo(
                        x0 + dx_i / 3f, y0 + slope[i] * dx_i / 3f,
                        x1 - dx_i / 3f, y1 - slope[i + 1] * dx_i / 3f,
                        x1, y1
                    )
                }

                return path
            }

            // Draw lines and dots for each yAxis line
            yAxes.forEachIndexed { lineIndex, yAxis ->
                val lineColor = lineColors.getOrElse(lineIndex) { Color.Black }
                val dotColor = dotColors.getOrElse(lineIndex) { Color.Black }

                // Keep original index with each point
                val indexedPoints = yAxis.mapIndexedNotNull { i, yValue ->
                    yValue?.let {
                        val x = startX + i * tickSpacingPx
                        val heightRatio = ((it - normMin) / rangeY).coerceIn(0.0, 1.0)
                        val barHeight = (heightRatio * usableHeight).toFloat()
                        val y = bottomY - baselineOffset - barHeight / 1.5f
                        i to Offset(x, y)
                    }
                }

                val points = indexedPoints.map { it.second }

                if (points.size >= 2) {
                    val path = createSmoothPath(points)
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Draw dots with correct selection logic
                indexedPoints.forEach { (originalIndex, point) ->
                    val isSelected = originalIndex == centerValue
                    drawCircle(
                        color = dotColor,
                        radius = if (isSelected) dotRadius * 1.2f else dotRadius,
                        center = point
                    )
                }
            }


            // Draw labels for xAxis
            xAxis.forEachIndexed { i, label ->
                val x = startX + i * tickSpacingPx
                if (x < -dotRadius || x > size.width + dotRadius) return@forEachIndexed

                val isSelected = i == centerValue

                val paint = Paint().apply {
                    color = if (isSelected) selectedLabelColor.toArgb() else unselectedLabelColor.toArgb()
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
            }
        }

    }
}



/*
Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value
            val dotRadius = dotSize.toPx()
            val maxBarHeight = size.height * 0.6f
            val bottomY = size.height * 0.75f

            labelBounds.clear()

            // Flatten and normalize all values across all lines
            val allNonNullValues = yAxes.flatten().filterNotNull()
            val normMin = allNonNullValues.minOrNull() ?: 0.0
            val normMax = allNonNullValues.maxOrNull() ?: 1.0
            val rangeY = (normMax - normMin).takeIf { it != 0.0 } ?: 1.0

            val baselineOffsetFraction = 0.33f
            val baselineOffset = maxBarHeight * baselineOffsetFraction
            val usableHeight = maxBarHeight - baselineOffset

            // Draw lines
            yAxes.forEachIndexed { lineIndex, yAxis ->
                val lineColor = lineColors.getOrElse(lineIndex) { Color.Black }

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

                if (points.size >= 2) {
                    val dashPath = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f, 20f)) // line Px, space Px
                    )

                    val normalPath = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    fun catmullRomPath(points: List<Offset>, tension: Float = 0.5f): Path {
                        val path = Path()
                        if (points.size < 2) return path

                        path.moveTo(points[0].x, points[0].y)

                        for (i in 1 until points.lastIndex) {
                            val p0 = points.getOrNull(i - 1) ?: points[i]
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val p3 = points.getOrNull(i + 2) ?: p2

                            val t = tension

                            val control1 = Offset(
                                p1.x + (p2.x - p0.x) * t / 6f,
                                p1.y + (p2.y - p0.y) * t / 6f
                            )
                            val control2 = Offset(
                                p2.x - (p3.x - p1.x) * t / 6f,
                                p2.y - (p3.y - p1.y) * t / 6f
                            )

                            path.cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                        }

                        return path
                    }
                    val path = catmullRomPath(points)

                    /*
                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }
                    */
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = if(lineIndex == 0){
                            //dashPath
                            normalPath
                        }else{
                            normalPath
                        }
                    )
                }
            }

            // Draw labels and dots (only once per x index)
            xAxis.forEachIndexed { i, label ->
                val x = startX + i * tickSpacingPx
                if (x < -dotRadius || x > size.width + dotRadius) return@forEachIndexed

                val isSelected = i == centerValue

                // Label
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

                /*
                // Dots for all lines
                yAxes.forEachIndexed { lineIndex, yAxis ->
                    val yValue = yAxis.getOrNull(i) ?: return@forEachIndexed
                    val heightRatio = ((yValue - normMin) / rangeY).coerceIn(0.0, 1.0)
                    val barHeight = (heightRatio * usableHeight).toFloat()
                    val y = bottomY - baselineOffset - barHeight / 1.5f

                    drawCircle(
                        color = dotColors.getOrElse(lineIndex) { Color.Black },
                        radius = if (isSelected) dotRadius * 1.2f else dotRadius,
                        center = Offset(x, y)
                    )
                }

                 */
            }
        }
 */