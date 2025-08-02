package com.xcvi.micros.ui.screens.goals


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Macros
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.core.keyboardOpenState
import com.xcvi.micros.ui.core.rememberShakeOffset
import com.xcvi.micros.ui.theme.carbsDark
import com.xcvi.micros.ui.theme.carbsLight
import com.xcvi.micros.ui.theme.fatsDark
import com.xcvi.micros.ui.theme.fatsLight
import com.xcvi.micros.ui.theme.proteinDark
import com.xcvi.micros.ui.theme.proteinLight


@Composable
fun ByCalories(
    goals: Macros,
    onConfirm: (protein: Int, carbs: Int, fats: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var calories by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        focusManager.clearFocus()
    }


    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .offset(x = shakeOffset)
            .padding(horizontal = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
    ) {
        item {
            TextField(
                value = calories,
                onValueChange = { calories = it },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 30.dp),
                shape = RoundedCornerShape(20.dp),
                label = { Text(stringResource(R.string.calorie_goal)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.NumberPassword
                ),
                placeholder = { Text("${goals.calories} kcal") }
            )
        }

        item {
            MacroSliders(
                calorieInput = calories,
                onConfirm = onConfirm,
                onDrag = { focusManager.clearFocus() },
                onError = { shakeTrigger = true }
            )
        }

        item {
            Text(
                text = stringResource(R.string.by_calories_info),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
            )
        }
    }
}


@Composable
fun MacroSliders(
    calorieInput: String,
    onConfirm: (protein: Int, carbs: Int, fats: Int) -> Unit,
    onDrag: () -> Unit,
    onError: () -> Unit,
) {
    val calories = calorieInput.toIntOrNull() ?: 0

    var pPercent by remember { mutableFloatStateOf(0f) }
    var cPercent by remember { mutableFloatStateOf(0f) }
    var fPercent by remember { mutableFloatStateOf(0f) }

    val pLabel = stringResource(R.string.protein)
    val cLabel = stringResource(R.string.carbs)
    val fLabel = stringResource(R.string.fats)

    val pValue = (pPercent / 400 * calories).roundToInt()
    val cValue = (cPercent / 400 * calories).roundToInt()
    val fValue = (fPercent / 900 * calories).roundToInt()

    val pText = "$pValue g"
    val cText = "$cValue g"
    val fText = "$fValue g"

    fun adjustSliders(changedMacro: String, newValue: Float) {
        val clampedValue = newValue.coerceIn(0f, 100f)

        val remaining = 100f - clampedValue

        when (changedMacro) {
            pLabel -> {
                val totalOther = cPercent + fPercent
                val carbRatio = if (totalOther == 0f) 0.5f else cPercent / totalOther
                val fatRatio = 1f - carbRatio

                pPercent = clampedValue
                cPercent = (remaining * carbRatio).coerceIn(0f, 100f)
                fPercent = (remaining * fatRatio).coerceIn(0f, 100f)
            }

            cLabel -> {
                val totalOther = pPercent + fPercent
                val proteinRatio = if (totalOther == 0f) 0.5f else pPercent / totalOther
                val fatRatio = 1f - proteinRatio

                cPercent = clampedValue
                pPercent = (remaining * proteinRatio).coerceIn(0f, 100f)
                fPercent = (remaining * fatRatio).coerceIn(0f, 100f)
            }

            fLabel -> {
                val totalOther = pPercent + cPercent
                val proteinRatio = if (totalOther == 0f) 0.5f else pPercent / totalOther
                val carbRatio = 1f - proteinRatio

                fPercent = clampedValue
                pPercent = (remaining * proteinRatio).coerceIn(0f, 100f)
                cPercent = (remaining * carbRatio).coerceIn(0f, 100f)
            }
        }
    }

    val isDark = isSystemInDarkTheme()

    val pColor = if (calories > 0) {
        if (isDark) {
            proteinDark
        } else {
            proteinLight
        }
    } else {
        MaterialTheme.colorScheme.onSurface.copy(0.5f)
    }

    val cColor = if (calories > 0) {
        if(isDark){
            carbsDark
        } else {
            carbsLight
        }
    } else {
        MaterialTheme.colorScheme.onSurface.copy(0.5f)
    }

    val fColor = if (calories > 0) {
        if(isDark){
            fatsDark
        } else {
            fatsLight
        }
    } else {
        MaterialTheme.colorScheme.onSurface.copy(0.5f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        CustomSlider(
            value = pPercent,
            onValueChange = { adjustSliders(pLabel, it) },
            onDragStart = onDrag,
            thumbColor = pColor
        )
        CustomSlider(
            value = cPercent,
            onValueChange = { adjustSliders(cLabel, it) },
            onDragStart = onDrag,
            thumbColor = cColor
        )
        CustomSlider(
            value = fPercent,
            onValueChange = { adjustSliders(fLabel, it) },
            onDragStart = onDrag,
            thumbColor = fColor
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            MacroValueText(pText, pPercent)
            MacroValueText(cText, cPercent, modifier = Modifier.padding(horizontal = 24.dp))
            MacroValueText(fText, fPercent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {


        val keyboardOpen by keyboardOpenState()
        Button(
            enabled = !keyboardOpen && calories > 0,
            onClick = {
                if (calories > 0 && pPercent + cPercent + fPercent > 0) {
                    onConfirm(pValue, cValue, fValue)
                } else {
                    onError()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
        ) {
            Text(text = stringResource(R.string.update))
        }

    }
}


@Composable
private fun MacroValueText(value: String, percentage: Float, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${percentage.roundToInt()}%",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
        )
    }
}


