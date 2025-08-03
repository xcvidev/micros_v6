package com.xcvi.micros.ui.core.comp

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.domain.utils.getTimestamp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateSelector(
    todayText: String,
    modifier: Modifier = Modifier,
    currentDate: Int,
    onDateChanged: (Int) -> Unit,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = 0.dp,
    showArrowKeys: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val localDate = getLocalDate(currentDate)
    val dateFormatted = "${
        localDate.dayOfWeekFormatted(true).replaceFirstChar { it.uppercase() }
    }, ${localDate.dayOfMonth} ${
        localDate.monthFormatted(true).replaceFirstChar { it.uppercase() }
    } "

    val buttonText = if (currentDate == getToday()) {
        todayText
    } else {
        dateFormatted
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (showArrowKeys) {
            IconButton(
                onClick = { onDateChanged(currentDate - 1) },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "",
                    tint = containerColor
                )
            }
        }

        Button(
            modifier = Modifier.weight(1f),
            //border = BorderStroke(1.dp,containerColor),
            onClick = {
                showDatePicker = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        ) {
            StreamingText(
                text = buttonText,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (showArrowKeys) {
            IconButton(
                enabled = currentDate < getToday(),
                onClick = { onDateChanged(currentDate + 1) },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = if (currentDate < getToday()) containerColor else MaterialTheme.colorScheme.onSurface.copy(
                        0.5f
                    )
                )
            }
        }
    }


    if (showDatePicker) {
        DateSelectorDialog(
            currentDate = currentDate,
            onDismissRequest = { showDatePicker = false },
            onDateChanged = onDateChanged
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectorDialog(
    currentDate: Int,
    onDismissRequest: () -> Unit,
    onDateChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showFutureDates: Boolean = false,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.getTimestamp(8, 0),
        selectableDates = if (showFutureDates) {
            DatePickerDefaults.AllDates
        } else { PastOrPresentSelectableDates }
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                modifier = modifier.padding(4.dp),
                onClick = {
                    onDismissRequest()
                    onDateChanged(
                        getLocalDateTime(
                            datePickerState.selectedDateMillis ?: getNow()
                        ).date.toEpochDays()
                    )
                }) {
                Text(text = "Ok")
            }
        },
        content = {
            Column(modifier.padding(top = 24.dp)) {
                DatePicker(
                    headline = null,
                    title = null,
                    state = datePickerState,
                    modifier = modifier.fillMaxWidth(),
                    showModeToggle = false,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= System.currentTimeMillis()
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year <= getLocalDate(getToday()).year
    }
}

fun LocalDate.dayOfWeekFormatted(
    short: Boolean = false,
    locale: Locale = Locale.getDefault(),
): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        this.dayOfWeek.getDisplayName(style, locale).lowercase().replaceFirstChar { it.uppercase() }
    } else {
        // Fallback: format manually (non-localized)
        val name = this.dayOfWeek.name
        if (short) name.substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
        else name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

private fun LocalDate.monthFormatted(
    short: Boolean = false,
    locale: Locale = Locale.getDefault(),
): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        this.month.getDisplayName(style, locale).replaceFirstChar { it.uppercase() }
    } else {
        // Fallback: format manually (non-localized)
        val name = this.month.name
        if (short) name.substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
        else name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

private fun getLocalDate(epochDays: Int): LocalDate {
    return LocalDate.fromEpochDays(epochDays)
}


private fun getNow(): Long {
    return System.currentTimeMillis()
}

private fun getToday(): Int {
    val instant = Instant.fromEpochMilliseconds(getNow())
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone).date.toEpochDays()
}

private fun getLocalDateTime(timestamp: Long): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone)
}