package com.xcvi.micros.ui.core


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.ui.core.comp.LoadingIndicator
import com.xcvi.micros.ui.core.comp.NumberPicker
import com.xcvi.micros.ui.core.utils.toLabeledPairs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    portion: Portion,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onScale: (Int) -> Unit,
    onEnhance: (String) -> Unit,
    isEnhancing: Boolean
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var showInputDialog by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    if (showInputDialog) {
        AlertDialog(
            onDismissRequest = { showInputDialog = false },
            title = { Text(text = stringResource(R.string.description)) },
            text = {
                Column {
                    Text(text = stringResource(R.string.provide_food_description))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        label = { Text(text = stringResource(R.string.description)) }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEnhance(input)
                        focusManager.clearFocus()
                        keyboard?.hide()
                        showInputDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.enhance_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showInputDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
        )
    }

    ModalBottomSheet(
        modifier = modifier
            .systemBarsPadding()
            .padding(horizontal = 8.dp),
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {

        LazyColumn(
            modifier = modifier
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = portion.food.name,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${portion.food.nutrients.calories} kcal, ${portion.amount} g",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            item {
                Spacer(modifier = Modifier.height(18.dp))
                NumberPicker(
                    initialValue = portion.amount,
                    onValueChange = {
                        if (it in 1..9999) {
                            onScale(it)
                        }
                    },
                    clickGranularity = 1,
                    onImeAction = {
                        keyboard?.hide()
                        focusManager.clearFocus()
                    },
                    valueRange = 1..10000
                )
            }
            item {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedButton(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        modifier = modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
                        onClick = {
                            showInputDialog = true
                        }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.enhance),
                                textAlign = TextAlign.Center
                            )
                            Text("\n")
                        }
                    }

                    Button(
                        modifier = modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        onClick = {
                            onConfirm()
                        }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.done),
                                color = MaterialTheme.colorScheme.surface
                            )
                            Text("\n")
                        }
                    }
                }
            }

            item {
                val nutrientsLabeled = portion.food.nutrients.toLabeledPairs(context)
                val mineralsLabeled = portion.food.minerals.toLabeledPairs(context)
                val vitaminsLabeled = portion.food.vitamins.toLabeledPairs(context)
                val aminoAcidsLabeled = portion.food.aminoAcids.toLabeledPairs(context)
                fun alpha(isEmpty: Boolean): Float = if (isEnhancing) {
                    0f
                } else {
                    if (isEmpty) {
                        0.4f
                    } else {
                        1f
                    }
                }

                val labels =
                    nutrientsLabeled + mineralsLabeled + vitaminsLabeled + aminoAcidsLabeled

                Box(contentAlignment = Alignment.Center) {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 16.dp),
                    ) {
                        labels.forEachIndexed { index: Int, value: Pair<String, String> ->
                            if (index > 0) {
                                val data = labels[index]
                                Row {
                                    Text(
                                        text = data.first,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha(
                                                data.second.startsWith("0.0") || data.second.startsWith(
                                                    "0,0"
                                                )
                                            )
                                        )
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = data.second,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha(
                                                data.second.startsWith("0.0") || data.second.startsWith(
                                                    "0,0"
                                                )
                                            )
                                        )
                                    )
                                }
                                if (index < labels.size - 1 && !isEnhancing) {
                                    HorizontalDivider(
                                        thickness = 0.3.dp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f)
                                    )
                                }
                            }
                        }
                    }
                    if (isEnhancing) {
                        LoadingIndicator()
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

