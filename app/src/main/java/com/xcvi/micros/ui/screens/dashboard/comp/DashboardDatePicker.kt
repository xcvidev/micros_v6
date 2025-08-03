package com.xcvi.micros.ui.screens.dashboard.comp

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.xcvi.micros.R
import com.xcvi.micros.domain.utils.getLocalDate
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.domain.utils.monthFormatted
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.core.comp.dayOfWeekFormatted
import com.xcvi.micros.ui.core.comp.rememberShakeOffset
import kotlinx.coroutines.launch
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.math.abs
import kotlin.math.absoluteValue

@Composable
fun DashboardDatePicker(
    centerValue: Int,
    onCenterValueChange: (Int) -> Unit,
    onCenterClick: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    spacing: Dp = 100.dp,
    fontSize: TextUnit = MaterialTheme.typography.headlineSmall.fontSize,
    fontWeight: Int = 500,
    sizeScaleDifference: Float = 0.35f,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    labelFormatter: (value: Int, offsetFromCenter: Float) -> String = { value, offset ->
        if (offset.absoluteValue < 0.1f) "$value" else value.toString()
    },
) {
    val textSizePx = with(LocalDensity.current) { fontSize.toPx() }
    val spacingPx = with(LocalDensity.current) { spacing.toPx() }



    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }

    val centerScale = 1.0f
    val sideScale =  centerScale - sizeScaleDifference
    val gestureOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()


    val itemPositions =
        remember { mutableMapOf<Int, Float>() }

    Box(
        modifier = modifier
            .offset(x = shakeOffset)
            .clipToBounds()
            .pointerInput(centerValue) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        scope.launch {
                            gestureOffset.snapTo(gestureOffset.value + dragAmount)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            val offset = gestureOffset.value
                            val shiftAmount = (offset / spacingPx).roundToInt()

                            if (shiftAmount != 0) {
                                gestureOffset.animateTo(shiftAmount * spacingPx, tween(150))
                                onCenterValueChange(centerValue - shiftAmount)
                                gestureOffset.snapTo(0f)
                            } else {
                                gestureOffset.animateTo(0f, tween(200))
                            }

                        }
                    }
                )
            }
            .pointerInput(centerValue) {
                detectTapGestures { tapOffset ->
                    val x = tapOffset.x
                    val screenCenterX = size.width / 2
                    val sideTapThreshold = spacingPx * 0.7f
                    val centerTapThreshold = spacingPx / 2 // or customize as needed

                    // Check if tap is on left item
                    itemPositions[-1]?.let { leftX ->
                        if (abs(x - leftX) < sideTapThreshold) {
                            scope.launch {
                                gestureOffset.animateTo(spacingPx, tween(300))
                                onCenterValueChange(centerValue - 1)
                                gestureOffset.snapTo(0f)
                            }
                            return@detectTapGestures
                        }
                    }

                    // Check if tap is on right item
                    itemPositions[1]?.let { rightX ->
                        if (abs(x - rightX) < sideTapThreshold) {
                            scope.launch {
                                gestureOffset.animateTo(-spacingPx, tween(300))
                                onCenterValueChange(centerValue + 1)
                                gestureOffset.snapTo(0f)
                            }
                            return@detectTapGestures
                        }
                    }

                    // Check if tap is near the center
                    if (abs(x - screenCenterX) < centerTapThreshold) {
                        onCenterClick()
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier
            .height(height)
            .fillMaxWidth()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            val offsetFraction = gestureOffset.value / spacingPx
            itemPositions.clear()

            for (i in -4..4) {
                if (abs(i + offsetFraction) > 1.5f) continue

                val value = centerValue + i
                val positionOffset = (i + offsetFraction) * spacingPx
                val absOffset = abs(i + offsetFraction)

                val scale = lerp(centerScale, sideScale, absOffset.coerceIn(0f, 1f))
                val alpha = lerp(1f, 0.4f, absOffset.coerceIn(0f, 1f))

                // Store left and right positions for hit testing
                if (i == -1) {
                    itemPositions[-1] = centerX + positionOffset
                } else if (i == 1) {
                    itemPositions[1] = centerX + positionOffset
                }


                val fontWeightScale = if (offsetFraction.absoluteValue < 0.1f) {
                    1.0f
                }else {
                    0.8f
                }
                val base = Typeface.create("sans-serif", Typeface.NORMAL)
                val font = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Typeface.create(base, (fontWeight* fontWeightScale).roundToInt(), false)
                } else {
                    base
                }



                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        color = textColor.toArgb()
                        textSize = textSizePx * scale
                        this.alpha = (alpha * 255).toInt()
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = font
                    }

                    val baselineOffset = paint.centeredBaselineOffset()
                    drawText(
                        label(value, context),
                        centerX + positionOffset,
                        centerY - baselineOffset,
                        paint
                    )
                }
            }
        }
    }
}
fun Paint.centeredBaselineOffset(): Float {
    return (descent() + ascent()) / 2
}
private fun label(date: Int, context: Context): String {
    val localDate = date.getLocalDate()
    val day = localDate.dayOfWeekFormatted(true)
    val month = localDate.monthFormatted(true)
    val today = getToday()

    val tag = when (date) {
        today -> context.getString(R.string.today) + ", ${localDate.dayOfMonth} $month"
        else -> "$day, ${localDate.dayOfMonth} $month"
    }
    return tag
}
