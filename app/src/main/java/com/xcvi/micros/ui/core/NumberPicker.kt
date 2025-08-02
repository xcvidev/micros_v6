package com.xcvi.micros.ui.core

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xcvi.micros.domain.utils.roundToInt
import kotlinx.coroutines.launch
import kotlin.math.abs


@SuppressLint("DefaultLocale")
@Composable
fun DecimalPicker(
    onImeAction: () -> Unit,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    tickColor: Color = MaterialTheme.colorScheme.onSurface,
    numberColor: Color = MaterialTheme.colorScheme.onSurface,
    indicatorTickColor: Color = MaterialTheme.colorScheme.onSurface,
    textFieldContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    valueRange: IntRange = 0..1000, // Represents 0.0 to 100.0
    initialValue: Int = 100,        // Represents 10.0
    tickSpacingDp: Dp = 12.dp,
    clickGranularity: Int = 10,
    horizontalClickTolerancePx: Float = 150f,
    verticalClickTolerancePx: Float = 100f,
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    weightUnitContent: @Composable () -> Unit,
) {
    val textSizePx = with(LocalDensity.current) { fontSize.toPx() }
    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx
    var isEditing by remember { mutableStateOf(false) }

    val scrollOffsetAnim =
        remember { Animatable((initialValue - valueRange.first) * tickSpacingPx) }
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt() + valueRange.first
    val clampedValue = centerValue.coerceIn(valueRange)
    val floatValue = clampedValue / 10f
    onValueChange(floatValue.toDouble())

    LaunchedEffect(scrollState.isScrollInProgress) {
        onImeAction()
        if (!scrollState.isScrollInProgress) {
            val nearestTick = (scrollOffsetAnim.value / tickSpacingPx).roundToInt()
            val targetOffset = nearestTick * tickSpacingPx
            scrollOffsetAnim.animateTo(targetOffset)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val tapX = offset.x
                    val centerX = size.width / 2f
                    val relativeX = scrollOffsetAnim.value + (tapX - centerX)
                    val tappedIndexFloat = relativeX / tickSpacingPx
                    val closestMultipleOf10Index =
                        ((tappedIndexFloat.roundToInt() / clickGranularity.toDouble()).roundToInt()) * clickGranularity
                    val tappedValue = valueRange.first + closestMultipleOf10Index

                    if (tappedValue in valueRange) {
                        val tickX =
                            centerX - scrollOffsetAnim.value + (tappedValue - valueRange.first) * tickSpacingPx
                        val tapY = offset.y
                        if (
                            abs(tapX - tickX) <= horizontalClickTolerancePx &&
                            tapY in (size.height / 2 - verticalClickTolerancePx)..(size.height / 2 + verticalClickTolerancePx)
                        ) {
                            val targetOffset = (tappedValue - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
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
            val totalTicks = valueRange.count()

            for (i in 0..totalTicks) {
                val x = startX + i * tickSpacingPx
                if (x < 0 || x > size.width) continue

                val value = valueRange.first + i
                val height = if (value % 5 == 0) 50f else 20f
                drawLine(
                    color = tickColor,
                    start = Offset(x, size.height / 2),
                    end = Offset(x, size.height / 2 - height),
                    strokeWidth = 2f
                )

                if (value % 10 == 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        String.format("%.1f", value / 10f),
                        x,
                        size.height / 2 + 80,
                        android.graphics.Paint().apply {
                            color = numberColor.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = textSizePx
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(2.5.dp)
                .height(800.dp)
                .background(MaterialTheme.colorScheme.primary)
        )

        var textValue by remember {
            mutableStateOf(
                TextFieldValue(
                    text = floatValue.toString()
                )
            )
        }

        LaunchedEffect(clampedValue, isEditing) {
            if (!isEditing && textValue.text != floatValue.toString()) {
                textValue = TextFieldValue(text = floatValue.toString())
            }
        }

        val focusRequester = remember { FocusRequester() }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-90).dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Text("")
            }
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .background(
                        color = textFieldContainerColor,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                TextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        val number = newValue.text.toDoubleOrNull()?.roundToInt()
                        if (newValue.text.isBlank() || (newValue.text.length <= 5 && number != null && number <= 999)) {
                            textValue = newValue
                            newValue.text.toFloatOrNull()?.let { entered ->
                                val intValue = (entered * 10).toInt()
                                if (intValue in valueRange) {
                                    val targetOffset = (intValue - valueRange.first) * tickSpacingPx
                                    scope.launch {
                                        scrollOffsetAnim.animateTo(targetOffset)
                                    }
                                }
                            }
                        }

                    },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && !isEditing) {
                                isEditing = true
                                textValue = textValue.copy(
                                    selection = TextRange(0, textValue.text.length)
                                )
                            } else if (!focusState.isFocused) {
                                isEditing = false
                            }
                        },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = numberColor,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onImeAction()
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                weightUnitContent()
            }
        }
    }

}


@Composable
fun NumberPicker(
    onImeAction: () -> Unit,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tickColor: Color = MaterialTheme.colorScheme.onSurface,
    numberColor: Color = MaterialTheme.colorScheme.onSurface,
    indicatorTickColor: Color = MaterialTheme.colorScheme.onSurface,
    textFieldContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    valueRange: IntRange = 1..10000,
    initialValue: Int = 100,
    tickSpacingDp: Dp = 12.dp,
    clickGranularity: Int = 10,
    horizontalClickTolerancePx: Float = 150f,
    verticalClickTolerancePx: Float = 100f,
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
) {
    val textSizePx = with(LocalDensity.current) { fontSize.toPx() }
    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx
    var isEditing by remember { mutableStateOf(false) }

    val scrollOffsetAnim =
        remember { Animatable((initialValue - valueRange.first) * tickSpacingPx) }
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt() + valueRange.first
    val clampedValue = centerValue.coerceIn(valueRange)
    onValueChange(clampedValue)


    // Snap to nearest tick after scroll ends
    LaunchedEffect(scrollState.isScrollInProgress) {
        onImeAction()
        if (!scrollState.isScrollInProgress) {
            val nearestTick = (scrollOffsetAnim.value / tickSpacingPx).roundToInt()
            val targetOffset = nearestTick * tickSpacingPx
            scrollOffsetAnim.animateTo(targetOffset)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->

                    val tapX = offset.x
                    val centerX = size.width / 2f
                    val relativeX = scrollOffsetAnim.value + (tapX - centerX)
                    val tappedIndexFloat = relativeX / tickSpacingPx

                    val closestMultipleOf10Index =
                        ((tappedIndexFloat.roundToInt() / clickGranularity.toDouble()).roundToInt()) * clickGranularity
                    val tappedValue = valueRange.first + closestMultipleOf10Index

                    if (tappedValue in valueRange) {
                        val tickX =
                            centerX - scrollOffsetAnim.value + (tappedValue - valueRange.first) * tickSpacingPx
                        val tapY = offset.y

                        if (
                            abs(tapX - tickX) <= horizontalClickTolerancePx &&
                            tapY in (size.height / 2 - verticalClickTolerancePx)..(size.height / 2 + verticalClickTolerancePx)
                        ) {
                            val targetOffset = (tappedValue - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
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
            val totalTicks = valueRange.count()

            for (i in 0..totalTicks) {
                val x = startX + i * tickSpacingPx
                if (x < 0 || x > size.width) continue

                val height = if ((valueRange.first + i) % 5 == 0) 30f else 15f
                drawLine(
                    color = tickColor,
                    start = Offset(x, size.height / 2),
                    end = Offset(x, size.height / 2 - height),
                    strokeWidth = 2f
                )

                if ((valueRange.first + i) % 10 == 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${valueRange.first + i}",
                        x,
                        size.height / 2 + 60,
                        android.graphics.Paint().apply {
                            color = numberColor.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = textSizePx
                        }
                    )
                }
            }
        }

        // Center indicator
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(2.dp)
                .height(60.dp)
                .background(MaterialTheme.colorScheme.primary)
        )


        var textValue by remember {
            mutableStateOf(
                TextFieldValue(
                    text = clampedValue.toString()
                )
            )
        }


        LaunchedEffect(clampedValue, isEditing) {
            if (!isEditing && textValue.text != clampedValue.toString()) {
                textValue = TextFieldValue(
                    text = clampedValue.toString()
                )
            }
        }

        val focusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-62).dp)
                .background(
                    color = textFieldContainerColor,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            TextField(
                value = textValue,
                onValueChange = { newValue ->
                    textValue = newValue
                    newValue.text.toIntOrNull()?.let { entered ->
                        if (entered in valueRange) {
                            val targetOffset = (entered - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(80.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        //isEditing = focusState.isFocused
                        if (focusState.isFocused && !isEditing) {
                            isEditing = true
                            textValue = textValue.copy(
                                selection = TextRange(0, textValue.text.length) // Select all
                            )
                        } else if (!focusState.isFocused) {
                            isEditing = false
                        }
                    },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = numberColor,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onImeAction()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

    }
}


