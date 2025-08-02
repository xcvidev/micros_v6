package com.xcvi.micros.ui.screens.scan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CameraOverlay(
    scanHintText: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val frameWidth = 300.dp
        val frameHeight = 180.dp
        val cornerLength = 30.dp
        val strokeWidth = 4.dp
        val cornerColor = Color.White.copy(alpha = 0.9f)

        val transition = rememberInfiniteTransition()
        val scanLineProgress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Draw dim background
            drawRect(
                color = Color.Black.copy(alpha = 0.3f),
                size = size
            )

            // Calculate frame position
            val frameSize = Size(frameWidth.toPx(), frameHeight.toPx())
            val frameTopLeft = Offset(
                (canvasWidth - frameSize.width) / 2f,
                (canvasHeight - frameSize.height) / 2f
            )
            val frameRect = Rect(frameTopLeft, frameSize)

            // Cut out the scanning box using BlendMode.Clear
            drawRect(
                color = Color.Transparent,
                topLeft = frameRect.topLeft,
                size = frameRect.size,
                blendMode = BlendMode.Clear
            )

            // Draw corners
            val strokePx = strokeWidth.toPx()
            val cornerPx = cornerLength.toPx()

            // Define corners relative to frame
            fun drawCorner(x: Float, y: Float, horizontal: Boolean, isStart: Boolean) {
                val dx = if (horizontal) cornerPx else 0f
                val dy = if (!horizontal) cornerPx else 0f
                drawLine(
                    color = cornerColor,
                    start = Offset(x, y),
                    end = Offset(x + if (isStart) dx else -dx, y + if (isStart) dy else -dy),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )
            }

            val left = frameRect.left
            val top = frameRect.top
            val right = frameRect.right
            val bottom = frameRect.bottom

            // Top-left
            drawCorner(left, top, true, true)
            drawCorner(left, top, false, true)

            // Top-right
            drawCorner(right, top, true, false)
            drawCorner(right, top, false, true)

            // Bottom-left
            drawCorner(left, bottom, true, true)
            drawCorner(left, bottom, false, false)

            // Bottom-right
            drawCorner(right, bottom, true, false)
            drawCorner(right, bottom, false, false)

            // Scan line animation
            val scanY = top + (frameSize.height * scanLineProgress)
            drawLine(
                color = cornerColor,
                start = Offset(left, scanY),
                end = Offset(right, scanY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Hint Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 200.dp)
        ) {
            Text(
                text = scanHintText,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )
        }
    }
}
