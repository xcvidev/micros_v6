package com.xcvi.micros.ui.screens.dashboard.comp


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

/**
 * Lifts current index control to parent. Enables onClick scroll
 */

@Composable
fun SwipableCardStack(
    cardCount: Int,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    height: Dp = 240.dp,
    expandedHeightFor: (index: Int) -> Dp,
    content: @Composable (index: Int, expanded: Boolean, height: Dp) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val cardWidthPx = remember { mutableFloatStateOf(0f) }

    val expandedStates = remember { mutableStateListOf(*Array(cardCount) { false }) }

    val swipeOffset = remember { Animatable(0f) }

    val isExpanded = expandedStates[currentIndex]
    val targetHeight = if (isExpanded) expandedHeightFor(currentIndex) else height
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = tween(300),
        label = "cardHeight"
    )

    // Reset swipe offset when index changes from outside (e.g. via dot click)
    LaunchedEffect(currentIndex) {
        swipeOffset.snapTo(0f)
    }

    val isDark = isSystemInDarkTheme()

    val topColor1 = if(isDark) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.onBackground.copy(0.02f)
    }
    val topColor2 = if(isDark){
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else{
        MaterialTheme.colorScheme.onBackground.copy(0.1f)
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 29.dp)
            .fillMaxWidth()
            .height(animatedHeight)
            .onGloballyPositioned { cardWidthPx.floatValue = it.size.width.toFloat() }
            .pointerInput(currentIndex) {
                detectDragGestures(
                    onDragEnd = {
                        val threshold = cardWidthPx.floatValue / 5
                        val offset = swipeOffset.value

                        scope.launch {
                            when {
                                offset > threshold -> {
                                    swipeOffset.animateTo(cardWidthPx.floatValue, tween(300))
                                    val next = (currentIndex + 1 + cardCount) % cardCount
                                    onIndexChange(next)
                                    swipeOffset.snapTo(0f)
                                }

                                offset < -threshold -> {
                                    swipeOffset.animateTo(-cardWidthPx.floatValue, tween(300))
                                    val next = (currentIndex + 1) % cardCount
                                    onIndexChange(next)
                                    swipeOffset.snapTo(0f)
                                }

                                else -> {
                                    swipeOffset.animateTo(0f, tween(200))
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            val newOffset = (swipeOffset.value + dragAmount.x).coerceIn(
                                -cardWidthPx.floatValue,
                                cardWidthPx.floatValue
                            )
                            swipeOffset.snapTo(newOffset)
                        }
                    }
                )
            }
    ) {
        val nextIndex = (currentIndex + 1) % cardCount
        val progress = kotlin.math.min(
            kotlin.math.abs(swipeOffset.value) / (cardWidthPx.floatValue.coerceAtLeast(1f)), 1f
        )

        val backCardCount = 2
        val baseOffsetDp = 20.dp
        val baseScaleStep = 0.04f
        val density = LocalDensity.current

        for (i in backCardCount downTo 1) {
            val idx = (currentIndex - i + cardCount) % cardCount
            val offsetPx = with(density) { baseOffsetDp.toPx() * i }
            val backgroundColor = when (i) {
                1 -> topColor1
                2 -> topColor2
                else -> MaterialTheme.colorScheme.onSurface
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animatedHeight)
                    .graphicsLayer {
                        translationX = -offsetPx
                        scaleX = 1f - baseScaleStep * i
                        scaleY = 1f - baseScaleStep * i
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                    }
                    .zIndex(-i.toFloat())
            ) {
                Box {
                    content(idx, false, height)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .matchParentSize()
                    )
                }
            }
        }

        for (i in backCardCount downTo 1) {
            val idx = (currentIndex + i) % cardCount
            val offsetPx = with(density) { baseOffsetDp.toPx() * i }
            val backgroundColor = when (i) {
                1 -> topColor1
                2 -> topColor2
                else -> MaterialTheme.colorScheme.onSurface
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animatedHeight)
                    .graphicsLayer {
                        translationX = offsetPx
                        scaleX = 1f - baseScaleStep * i
                        scaleY = 1f - baseScaleStep * i
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                    }
                    .zIndex(-i.toFloat())
            ) {
                Box {
                    content(idx, false, height)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .matchParentSize()
                    )
                }
            }
        }

        // Next card preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .graphicsLayer {
                    scaleX = 0.95f + 0.05f * progress
                    scaleY = 0.95f + 0.05f * progress
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                }
                .zIndex(0f)
        ) {
            content(nextIndex, expandedStates[nextIndex], height)
        }

        // Top card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .graphicsLayer {
                    translationX = swipeOffset.value
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                   shadowElevation = 24f
                }
                .zIndex(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable{expandedStates[currentIndex] = !expandedStates[currentIndex] }
        ) {
            content(currentIndex, expandedStates[currentIndex], animatedHeight)
        }
    }
}


/**
 * Keeps current index control private.
 */

/*
@Composable
fun SwipableCardStack(
    cardCount: Int = 4,
    height: Dp = 240.dp,
    expandedHeightFor: (index: Int) -> Dp,
    onIndexChanged: (Int) -> Unit = {},
    content: @Composable (index: Int, expanded: Boolean, height: Dp) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val cardWidthPx = remember { mutableFloatStateOf(0f) }

    var currentIndex by remember { mutableIntStateOf(0) }
    val expandedStates = remember { mutableStateListOf(*Array(cardCount) { false }) }

    // Swipe horizontal offset of top card
    val swipeOffset = remember { Animatable(0f) }


    val isExpanded = expandedStates[currentIndex]
    val targetHeight = if (isExpanded) expandedHeightFor(currentIndex) else height
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = tween(300),
        label = "cardHeight"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 29.dp) // important to center the cards
            .fillMaxWidth()
            .height(animatedHeight)
            .onGloballyPositioned { cardWidthPx.floatValue = it.size.width.toFloat() }
            .pointerInput(currentIndex) {
                detectDragGestures(
                    onDragEnd = {
                        val threshold = cardWidthPx.floatValue / 4
                        val offset = swipeOffset.value

                        scope.launch {
                            when {
                                offset > threshold -> {
                                    // Swipe right - animate out
                                    swipeOffset.animateTo(cardWidthPx.floatValue, tween(300))
                                    currentIndex = (currentIndex + 1 + cardCount) % cardCount
                                    swipeOffset.snapTo(0f)
                                    onIndexChanged(currentIndex)
                                }

                                offset < -threshold -> {
                                    // Swipe left - animate out
                                    swipeOffset.animateTo(-cardWidthPx.floatValue, tween(300))
                                    currentIndex = (currentIndex + 1) % cardCount
                                    swipeOffset.snapTo(0f)
                                    onIndexChanged(currentIndex)
                                }

                                else -> {
                                    // Snap back
                                    swipeOffset.animateTo(0f, tween(200))
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            val newOffset =
                                (swipeOffset.value + dragAmount.x).coerceIn(
                                    -cardWidthPx.floatValue,
                                    cardWidthPx.floatValue
                                )
                            swipeOffset.snapTo(newOffset)
                        }
                    }
                )
            }
    ) {
        val nextIndex = (currentIndex + 1) % cardCount
        val progress = kotlin.math.min(
            kotlin.math.abs(swipeOffset.value) / (cardWidthPx.floatValue.coerceAtLeast(1f)), 1f
        )

        // Fake stacked cards behind (just static small scaled/offset versions)
        val backCardCount = 2
        val baseOffsetDp = 20.dp
        val baseScaleStep = 0.04f
        val baseColor = MaterialTheme.colorScheme.surfaceContainer

        val density = LocalDensity.current

        // Draw left fake cards (before currentIndex)
        for (i in backCardCount downTo 1) {
            val idx = (currentIndex - i + cardCount) % cardCount
            val offsetPx = with(density) { baseOffsetDp.toPx() * i }
            val backgroundColor = when (i) {
                //1 -> MaterialTheme.colorScheme.onSurface.copy(0.05f)
                //2 -> MaterialTheme.colorScheme.onSurface.copy(0.1f)
                1 -> MaterialTheme.colorScheme.surfaceContainerHighest
                2 -> MaterialTheme.colorScheme.surfaceContainerHigh
                else -> MaterialTheme.colorScheme.onSurface
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        translationX = -offsetPx
                        scaleX = 1f - baseScaleStep * i
                        scaleY = 1f - baseScaleStep * i
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                    }
                    .zIndex(-i.toFloat())
            ) {
                Box {
                    content(idx, false, height)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .matchParentSize()
                    )
                }

            }
        }

        // Draw right fake cards (after currentIndex)
        for (i in backCardCount downTo 1) {
            val idx = (currentIndex + i) % cardCount
            val offsetPx = with(density) { baseOffsetDp.toPx() * i }
            val backgroundColor = when (i) {
                1 -> MaterialTheme.colorScheme.surfaceContainerHighest
                2 -> MaterialTheme.colorScheme.surfaceContainerHigh
                else -> MaterialTheme.colorScheme.onSurface
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        translationX = offsetPx
                        scaleX = 1f - baseScaleStep * i
                        scaleY = 1f - baseScaleStep * i
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                    }
                    .zIndex(-i.toFloat())
            ) {
                Box {
                    content(idx, false, height)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .matchParentSize()
                    )
                }
            }
        }

        // Next card - subtle scale and alpha animation based on swipe progress
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = 0.95f + 0.05f * progress
                    scaleY = 0.95f + 0.05f * progress
                    //alpha = 0.8f + 0.2f * progress
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                }
                .zIndex(0f)

        ) {
            content(nextIndex, expandedStates[nextIndex], height)
        }

        // Top card with horizontal swipe offset and clickable to expand
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationX = swipeOffset.value
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                    shadowElevation = 24f
                }
                .zIndex(1f)
                .clickable {
                    expandedStates[currentIndex] = !expandedStates[currentIndex]
                }
        ) {
            content(currentIndex, expandedStates[currentIndex], animatedHeight)
        }
    }
}


@Composable
fun SwipableCardStack(
    cardCount: Int = 4,
    height: Dp = 240.dp,
    expandedHeight: Dp = 320.dp,
    onIndexChanged: (Int) -> Unit = {},
    content: @Composable (index: Int, expanded: Boolean, height: Dp) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val cardWidthPx = remember { mutableFloatStateOf(0f) }

    var currentIndex by remember { mutableIntStateOf(0) }
    val expandedStates = remember { mutableStateListOf(*Array(cardCount) { false }) }

    // Swipe horizontal offset of top card
    val swipeOffset = remember { Animatable(0f) }


    val isExpanded = expandedStates[currentIndex]
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) expandedHeight else height,
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 29.dp) // important to center the cards
            .fillMaxWidth()
            .height(animatedHeight)
            .onGloballyPositioned { cardWidthPx.floatValue = it.size.width.toFloat() }
            .pointerInput(currentIndex) {
                detectDragGestures(
                    onDragEnd = {
                        val threshold = cardWidthPx.floatValue / 4
                        val offset = swipeOffset.value

                        scope.launch {
                            when {
                                offset > threshold -> {
                                    // Swipe right - animate out
                                    swipeOffset.animateTo(cardWidthPx.floatValue, tween(300))
                                    currentIndex = (currentIndex + 1 + cardCount) % cardCount
                                    swipeOffset.snapTo(0f)
                                    onIndexChanged(currentIndex)
                                }

                                offset < -threshold -> {
                                    // Swipe left - animate out
                                    swipeOffset.animateTo(-cardWidthPx.floatValue, tween(300))
                                    currentIndex = (currentIndex + 1) % cardCount
                                    swipeOffset.snapTo(0f)
                                    onIndexChanged(currentIndex)
                                }

                                else -> {
                                    // Snap back
                                    swipeOffset.animateTo(0f, tween(200))
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            val newOffset =
                                (swipeOffset.value + dragAmount.x).coerceIn(
                                    -cardWidthPx.floatValue,
                                    cardWidthPx.floatValue
                                )
                            swipeOffset.snapTo(newOffset)
                        }
                    }
                )
            }
    ) {
        val nextIndex = (currentIndex + 1) % cardCount
        val progress = kotlin.math.min(
            kotlin.math.abs(swipeOffset.value) / (cardWidthPx.floatValue.coerceAtLeast(1f)), 1f
        )

        // Fake stacked cards behind (just static small scaled/offset versions)
        val backCardCount = 2
        val baseOffsetDp = 20.dp
        val baseScaleStep = 0.04f
        val baseColor = MaterialTheme.colorScheme.primary

        val density = LocalDensity.current

        // Draw left fake cards (before currentIndex)
        for (i in backCardCount downTo 1) {
            val idx = (currentIndex - i + cardCount) % cardCount
            val offsetPx = with(density) { baseOffsetDp.toPx() * i }
            val backgroundColor = when (i) {
                1 -> MaterialTheme.colorScheme.onSurface.copy(0.05f)
                2 -> MaterialTheme.colorScheme.onSurface.copy(0.1f)
                else -> MaterialTheme.colorScheme.onSurface
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        translationX = -offsetPx
                        scaleX = 1f - baseScaleStep * i
                        scaleY = 1f - baseScaleStep * i
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                    }
                    .zIndex(-i.toFloat())
            ) {
                Box {
                    content(idx, false, height)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .matchParentSize()
                    )
                }

            }
        }

        // Draw right fake cards (after currentIndex)
        for (i in backCardCount downTo 1) {
            val idx = (currentIndex + i) % cardCount
            val offsetPx = with(density) { baseOffsetDp.toPx() * i }
            val backgroundColor = when (i) {
                1 -> MaterialTheme.colorScheme.onSurface.copy(0.05f)
                2 -> MaterialTheme.colorScheme.onSurface.copy(0.1f)
                else -> MaterialTheme.colorScheme.onSurface
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        translationX = offsetPx
                        scaleX = 1f - baseScaleStep * i
                        scaleY = 1f - baseScaleStep * i
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                    }
                    .zIndex(-i.toFloat())
            ) {
                Box {
                    content(idx, false, height)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .matchParentSize()
                    )
                }
            }
        }

        // Next card - subtle scale and alpha animation based on swipe progress
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = 0.95f + 0.05f * progress
                    scaleY = 0.95f + 0.05f * progress
                    //alpha = 0.8f + 0.2f * progress
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                }
                .zIndex(0f)

        ) {
            content(nextIndex, expandedStates[nextIndex], height)
        }

        // Top card with horizontal swipe offset and clickable to expand
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationX = swipeOffset.value
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                    shadowElevation = 24f
                }
                .zIndex(1f)
                .clickable {
                    expandedStates[currentIndex] = !expandedStates[currentIndex]
                }
        ) {
            content(currentIndex, expandedStates[currentIndex], animatedHeight)
        }
    }
}
*/