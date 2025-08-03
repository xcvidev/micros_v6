package com.xcvi.micros.ui.core.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun HorizontalFadedBox(
    height: Dp,
    horizontalFade: Dp,
    modifier: Modifier = Modifier,
    targetColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        // LEFT FADE: background → transparent
        Box(
            modifier = Modifier
                .height(height)
                .width(horizontalFade)
                .align(Alignment.CenterStart)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(targetColor, Color.Transparent)
                    )
                )
        )

        // RIGHT FADE: transparent → background
        Box(
            modifier = Modifier
                .height(height)
                .width(horizontalFade)
                .align(Alignment.CenterEnd)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, targetColor)
                    )
                )
        )

    }
}

@Composable
fun VerticalFadedBox(
    width: Dp,
    verticalFade: Dp,
    modifier: Modifier = Modifier,
    targetColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()

        // TOP FADE: background → transparent
        Box(
            modifier = Modifier
                .width(width)
                .height(verticalFade)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(targetColor, Color.Transparent)
                    )
                )
        )

        // BOTTOM FADE: transparent → background
        Box(
            modifier = Modifier
                .width(width)
                .height(verticalFade)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, targetColor)
                    )
                )
        )
    }
}