package com.xcvi.micros.ui.core

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun StreamingText(
    text: String,
    modifier: Modifier = Modifier,
    charDelayMillis: Long = 30L,
    initialDelayMillis: Long = 0L,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    onFinished: (() -> Unit)? = null,
) {
    var visibleText by remember { mutableStateOf("") }
    val lines = text.split("\n").size

    LaunchedEffect(text) {
        delay(initialDelayMillis)
        visibleText = ""
        for (i in text.indices) {
            visibleText += text[i]
            delay(charDelayMillis)
        }
        onFinished?.invoke()
    }

    Box{
        Text(
            text = visibleText,
            modifier = modifier,
            color = color,
            style = style,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = lines,
            minLines = lines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/*
@Composable
fun StreamingTextCard(
    title: String,
    subtitle: String,
    body: String,
    modifier: Modifier = Modifier,
    charDelayMillis: Long = 30L,
    onClick: () -> Unit,
    onFinished: (() -> Unit)? = null,
) {
    var visibleTitle by remember(title) { mutableStateOf("") }
    var visibleSubtitle by remember(subtitle) { mutableStateOf("") }
    var visibleBody by remember(body) { mutableStateOf("") }
    var hasAnimated by remember(title + subtitle + body) { mutableStateOf(false) }

    LaunchedEffect(hasAnimated) {
        if (!hasAnimated) {
            visibleTitle = streamText(title, charDelayMillis) { visibleTitle = it }
            visibleSubtitle = streamText(subtitle, charDelayMillis) { visibleSubtitle = it }
            visibleBody = streamText(body, charDelayMillis) { visibleBody = it }
            hasAnimated = true
            onFinished?.invoke()
        }
    }

    Card(
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = visibleTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = visibleSubtitle, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = visibleBody, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

suspend fun streamText(
    text: String,
    delayMillis: Long,
    onUpdate: (String) -> Unit
): String {
    var visible = ""
    for (char in text) {
        visible += char
        onUpdate(visible)
        delay(delayMillis)
    }
    return visible
}

 */

@Composable
fun FadingText(
    text: String,
    modifier: Modifier = Modifier,
    animationDuration: Int = 300,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
) {

    Box(modifier = modifier){
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                fadeIn(tween(animationDuration)) togetherWith fadeOut(tween(animationDuration))
            },
            label = "AnimatedText"
        ) { targetText ->
            Text(
                text = targetText,
                color = color,
                style = style,
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = textAlign,
            )
        }
    }
}


@Composable
fun SlidingText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 14,
    animationDuration: Int = 300,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(modifier = modifier){
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = animationDuration),
                    initialOffsetX = { fullWidth -> fullWidth } // Slide in from right
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = animationDuration
                    ),
                    targetOffsetX = { fullWidth -> -fullWidth } // Slide out to left
                )
            },
            label = "SlideTextAnimation"
        ) { targetText ->
            Text(
                text = targetText,
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                color = color
            )
        }
    }
}
