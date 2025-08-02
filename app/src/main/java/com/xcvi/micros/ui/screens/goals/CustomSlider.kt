package com.xcvi.micros.ui.screens.goals

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: IntRange = 0..100,
    thumbRadius: Dp = 8.dp,
    trackHeight: Dp = 7.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    thumbColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
    sliderWidth: Dp = 280.dp,
    onDragStart: (() -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null,
    isDragEnabled: Boolean = true
) {
    val density = LocalDensity.current
    val thumbRadiusPx = with(density) { thumbRadius.toPx() }
    val trackHeightPx = with(density) { trackHeight.toPx() }

    val totalSteps = valueRange.last - valueRange.first

    BoxWithConstraints(
        modifier = modifier
            .width(sliderWidth)
            .height(thumbRadius * 2)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val valueRatio = (value - valueRange.first) / totalSteps
        val thumbCenterX = valueRatio * widthPx

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    if(isDragEnabled){
                        detectDragGestures(
                            onDragStart = {
                                onDragStart?.invoke()
                            },
                            onDragEnd = {
                                onDragEnd?.invoke()
                            },
                            onDrag = { change, _ ->
                                val dragRatio = (change.position.x / widthPx).coerceIn(0f, 1f)
                                val newValue = (valueRange.first + dragRatio * totalSteps)
                                onValueChange(newValue)
                            }
                        )
                    }
                }
        ) {
            // Track
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(0f, center.y - trackHeightPx / 2),
                size = Size(size.width, trackHeightPx),
                cornerRadius = CornerRadius(trackHeightPx / 2)
            )

            // Thumb
            drawCircle(
                color = thumbColor,
                radius = thumbRadiusPx,
                center = Offset(thumbCenterX, center.y)
            )
        }
    }
}


/*
@Composable
fun CustomSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: IntRange = 1..100,
    thumbRadius: Dp = 8.dp,
    trackHeight: Dp = 2.dp,
    trackColor: Color = Color.Gray,
    thumbColor: Color = Color.Black,
    sliderWidth: Dp = 280.dp
) {
    val density = LocalDensity.current
    val thumbRadiusPx = with(density) { thumbRadius.toPx() }
    val trackHeightPx = with(density) { trackHeight.toPx() }

    val totalSteps = valueRange.last - valueRange.first

    BoxWithConstraints(
        modifier = modifier
            .width(sliderWidth)
            .height(thumbRadius * 2)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val valueRatio = (value - valueRange.first).toFloat() / totalSteps
        val thumbCenterX = valueRatio * widthPx

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val touchRatio = (offset.x / widthPx).coerceIn(0f, 1f)
                            val newValue = (valueRange.first + touchRatio * totalSteps).roundToInt()
                            onValueChange(newValue)
                        },
                        onDrag = { change, _ ->
                            val dragRatio = (change.position.x / widthPx).coerceIn(0f, 1f)
                            val newValue = (valueRange.first + dragRatio * totalSteps).roundToInt()
                            onValueChange(newValue)
                        }
                    )
                }
        ) {
            // Track
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(0f, center.y - trackHeightPx / 2),
                size = Size(size.width, trackHeightPx),
                cornerRadius = CornerRadius(trackHeightPx / 2)
            )

            // Thumb
            drawCircle(
                color = thumbColor,
                radius = thumbRadiusPx,
                center = Offset(thumbCenterX, center.y)
            )
        }
    }
}

 */

