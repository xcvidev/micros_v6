package com.xcvi.micros.ui.screens.goals


import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Macros
import com.xcvi.micros.ui.core.rememberShakeOffset

@Composable
fun ByMacros(
    goals: Macros,
    onConfirm: (protein: Int, carbs: Int, fats: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
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
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .pointerInput(Unit) {
                detectDragGestures(onDrag = { _, _ -> focusManager.clearFocus() })
            },
    ) {

        item {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = protein,
                    onValueChange = {
                        val intVal = it.toIntOrNull()
                        protein = intVal?.toString() ?: ""
                    },
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    label = { Text(stringResource(R.string.protein)) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    placeholder = { Text("${goals.protein} g") }
                )

                TextField(
                    value = carbs,
                    onValueChange = {
                        val intVal = it.toIntOrNull()
                        carbs = intVal?.toString() ?: ""
                    },
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    label = { Text(stringResource(R.string.carbs)) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    placeholder = { Text("${goals.carbohydrates} g") }
                )
                TextField(
                    value = fats,
                    onValueChange = {
                        val intVal = it.toIntOrNull()
                        fats = intVal?.toString() ?: ""
                    },
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    label = {

                        Text(stringResource(R.string.fats)) },
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
                    placeholder = { Text("${goals.fats} g") }
                )

            }
        }
        item {
            Text(
                text = "${
                    (protein.toIntOrNull() ?: 0) * 4
                            + (carbs.toIntOrNull() ?: 0) * 4
                            + (fats.toIntOrNull() ?: 0) * 9
                } kcal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f),
                textAlign = TextAlign.Center
            )
        }

        item {
            Button(
                enabled = protein.isNotBlank() && carbs.isNotBlank() && fats.isNotBlank(),
                onClick = {
                    val proteinVal = protein.toIntOrNull()
                    val carbsVal = carbs.toIntOrNull()
                    val fatsVal = fats.toIntOrNull()
                    if (proteinVal != null && carbsVal != null && fatsVal != null){
                        onConfirm(proteinVal, carbsVal, fatsVal)
                    } else {
                        shakeTrigger = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 24.dp)
            ) {
                Text(stringResource(R.string.update))
            }
        }

        item {
            Text(
                text = stringResource((R.string.by_macros_info)),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
            )
        }

    }
}
