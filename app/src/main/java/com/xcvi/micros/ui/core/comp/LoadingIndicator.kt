package com.xcvi.micros.ui.core.comp

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun AnimatedDots(
    baseText: String = "",
    dotCount: Int = 3,
    delayMillis: Long = 300,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
) {
    var dotState by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            dotState = (dotState + 1) % (dotCount + 1)
            delay(delayMillis)
        }
    }

    Text(
        text = baseText + ".".repeat(dotState),
        modifier = modifier,
        maxLines = 1,
        color = color,
        style = style,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
    )
}


@Composable
fun SmoothProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    durationMillis: Int = 500,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceBright
) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = durationMillis, easing = LinearOutSlowInEasing)
    )
    LinearProgressIndicator(
        modifier = modifier,
        progress = { animatedProgress.value },
        color = color,
        trackColor = trackColor,
        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
        gapSize = 0.dp,
        drawStopIndicator = {}
    )
}
