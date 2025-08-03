package com.xcvi.micros.ui.screens.weight

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.weight.Weight
import com.xcvi.micros.domain.model.weight.WeightUnit
import com.xcvi.micros.domain.utils.formatEpochDate
import com.xcvi.micros.domain.utils.getEndOfWeek
import com.xcvi.micros.domain.utils.getEpochDate
import com.xcvi.micros.domain.utils.getLocalDate
import com.xcvi.micros.domain.utils.getLocalDateTime
import com.xcvi.micros.domain.utils.getStartOfWeek
import com.xcvi.micros.domain.utils.getTime
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.domain.utils.roundDecimals
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.core.comp.DateSelectorDialog
import com.xcvi.micros.ui.core.comp.DecimalPicker
import com.xcvi.micros.ui.core.comp.dayOfWeekFormatted
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    state: WeightState,
    onEvent: (WeightEvent) -> Unit,
    modifier: Modifier = Modifier,
) {

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val haptics = LocalHapticFeedback.current

    var showDeleteDialog by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }



    if (showDatePicker) {
        DateSelectorDialog(
            showFutureDates = false,
            currentDate = state.currentDate,
            onDismissRequest = { showDatePicker = false },
            onDateChanged = { onEvent(WeightEvent.SetDate(it)) }
        )
    }



    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = stringResource(R.string.delete), fontSize = 24.sp)
            },
            text = { Text(stringResource(R.string.delete_confirm_text)) },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(WeightEvent.ConfirmDelete)
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                }
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.weight_manager)) },
                actions = {
                    TextButton(
                        onClick = {
                            showDatePicker = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            "",
                        )
                    }
                }
            )
        },
    ) { padding ->

        var isEditing by remember { mutableStateOf(false) }
        LazyColumn(
            modifier = modifier.padding(padding)
        ) {
            item {
                Spacer(modifier = modifier.height(120.dp))
            }
            item {
                AnimatedVisibility(
                    visible = state.initialValue != null,
                    enter = slideInVertically(animationSpec = tween(300)),
                    exit = slideOutVertically(animationSpec = tween(300))
                ) {
                    state.initialValue?.let { initialValue ->
                        DecimalPicker(
                            initialValue = (initialValue * 10).roundToInt(),
                            onValueChange = {
                                onEvent(WeightEvent.SetPickerValue(it))
                            },
                            valueRange = 0..30000,
                            clickGranularity = 1,
                            onImeAction = {
                                keyboard?.hide()
                                focusManager.clearFocus()
                            },

                            ) {
                            Card(
                                modifier = modifier.padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),

                                ) {
                                TextButton(onClick = {
                                    onEvent(WeightEvent.ToggleUnit)
                                }) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = modifier.padding(start = 4.dp, end = 4.dp),
                                            text = state.unit.name.replaceFirstChar { it.uppercase() },
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
            item {
                SaveButton(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp)) {
                    isEditing = false
                    onEvent(WeightEvent.Save())
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    keyboard?.hide()
                    focusManager.clearFocus()
                }
            }
            item {
                Spacer(modifier = modifier.height(12.dp))
            }
            item {
                WeightSummary(
                    list = state.weights,
                    onClick = { weight ->
                        onEvent(WeightEvent.SetDeleteWeight(weight))
                        showDeleteDialog = true
                    },
                    startDateIfEmpty = state.currentDate
                )
            }

            item {
                Spacer(modifier = modifier.height(120.dp))
            }

        }
    }
}

@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var saved by remember { mutableStateOf(false) }

    // Trigger reset animation after a save
    LaunchedEffect(saved) {
        if (saved) {
            delay(1500) // Reset after 800ms
            saved = false
        }
    }

    val transition = updateTransition(targetState = saved, label = "")

    val iconAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "icon_alpha"
    ) { isSaved -> if (isSaved) 1f else 0f }

    val buttonColor by transition.animateColor(
        label = ""
    ) { isSaved ->
        if (isSaved) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.primary
    }

    Button(
        modifier = modifier,
        onClick = {
            saved = true
            onClick()
        },
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        if (saved) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "",
                modifier = Modifier.alpha(iconAlpha),
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = stringResource(R.string.save),
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}


@Composable
fun WeightSummary(
    list: List<Weight>,
    onClick: (Weight) -> Unit,
    startDateIfEmpty: Int = 0,
) {
    if (list.isEmpty()) {
        val start = startDateIfEmpty.getStartOfWeek().formatEpochDate(short = false)
        val end = startDateIfEmpty.getEndOfWeek().formatEpochDate(short = false)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = 16.dp
                )
        ) {
            // Date range title
            Text(
                text = "$start – $end",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(label = stringResource(R.string.min), value = 0.0.toString(), null)
                SummaryItem(label = stringResource(R.string.avg), value = 0.0.toString(), null)
                SummaryItem(label = stringResource(R.string.max), value = 0.0.toString(), null)
            }

            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = stringResource(R.string.weight_empty_data),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
            )
        }
    } else {

        val min = list.minOfOrNull { it.weight }?.roundDecimals() ?: 0.0
        val max = list.maxOfOrNull { it.weight }?.roundDecimals() ?: 0.0
        val avg = (list.sumOf { it.weight } / list.size).roundDecimals()
        val unit = list.firstOrNull()?.unit ?: WeightUnit.kg
        val date = list.firstOrNull()?.timestamp?.getEpochDate() ?: getToday()
        val start = date.getStartOfWeek().formatEpochDate(short = false)
        val end = date.getEndOfWeek().formatEpochDate(short = false)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            // Date range title
            Text(
                text = "$start – $end",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = stringResource(R.string.min),
                    value = min.roundDecimals().toString(),
                    unit = unit.name
                )
                SummaryItem(
                    label = stringResource(R.string.avg),
                    value = avg.roundDecimals().toString(),
                    unit = unit.name
                )
                SummaryItem(
                    label = stringResource(R.string.max),
                    value = max.roundDecimals().toString(),
                    unit = unit.name
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Weight entries
            list.forEachIndexed { index, weight ->
                if (index > 0) {
                    HorizontalDivider(thickness = 0.3.dp)
                }
                WeightEntry(
                    weight = weight,
                    onClick = { onClick(weight) }
                )
                if (index < list.size - 1) {
                    HorizontalDivider(thickness = 0.3.dp)
                }
            }

        }
    }


}

@Composable
fun SummaryItem(label: String, value: String, unit: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (unit != null) {
                Text(
                    text = " $unit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeightEntry(
    weight: Weight,
    onClick: () -> Unit = {},
) {

    val interactionSource = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { /* optional */ },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
    ) {
        HorizontalDivider(thickness = 0.3.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = weight.weight.roundDecimals().toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 24.dp)
            )
            Text(
                text = weight.unit.name,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        Text(
            text = weight.date.getLocalDate()
                .dayOfWeekFormatted(false) + " - " + weight.timestamp.getLocalDateTime()
                .getTime(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}



