package com.xcvi.micros.ui.screens.meal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.ui.core.comp.OnNavigation
import com.xcvi.micros.ui.core.comp.rememberShakeOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreen(
    number: Int,
    date: Int,
    label: String,
    state: MealState,
    onEvent: (MealEvent) -> Unit,
    onBack: () -> Unit,
    onGotoAdd: () -> Unit,
    onGotoDetails: (Portion) -> Unit,
    modifier: Modifier = Modifier,
) {

    OnNavigation {
        onEvent(MealEvent.GetMeal(date = date, number = number))
    }
    val inputDialogTitle: String = stringResource(R.string.save)
    val inputDialogPlaceholder: String = stringResource(R.string.meal_enter_name)

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current



    Scaffold(
        modifier = modifier
            .offset(x = shakeOffset)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(label) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                /*
                actions = {
                    if (state.portions.isNotEmpty()) {
                        TextButton(onClick = { onBack() }) {
                            Text(
                                text = stringResource(R.string.done),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } */
            )
        }
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            if (state.portions.isEmpty()) {
                item {
                    EmptyMealContent {
                        onGotoAdd()
                    }
                }
            } else {
                item {
                    MealSummaryCard(
                        nutrients = state.nutrients,
                        minerals = state.minerals,
                        vitamins = state.vitamins,
                        aminoAcids = state.aminoAcids,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        // verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.portions.forEachIndexed { index, portion ->
                            if (index > 0) {
                                HorizontalDivider(
                                    thickness = 0.3.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }

                            ItemCard(
                                portion = portion,
                                onChangeAmount = { isIncrease ->
                                    onEvent(
                                        MealEvent.UpdatePortion(
                                            portion,
                                            isIncrease
                                        ) { showDeleteDialog = true }
                                    )
                                },
                                onClick = { onGotoDetails(portion) },
                                onLongClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onEvent(MealEvent.SelectPortion(portion))
                                    showDeleteDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                if (state.portions.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { onGotoAdd() },
                                modifier = modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.food_add),
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                            //ExpandableMicrosSection(meal = meal)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }


        }
    }
    if (showInputDialog) {
        InputDialog(
            title = inputDialogTitle,
            placeholder = inputDialogPlaceholder,
            onDismiss = { showInputDialog = false },
            onConfirm = { name ->

            },
            offset = shakeOffset
        )
    }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                onEvent(MealEvent.DeletePortion)
                showDeleteDialog = false
            },
            offset = shakeOffset,
        )
    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    portion: Portion,
    onChangeAmount: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(onClick = { onClick() }, onLongClick = { onLongClick() })
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = portion.name,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "${portion.nutrients.calories} kcal • ${portion.amount} g",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // +/- buttons
            Row(modifier = Modifier.padding(8.dp)) {
                IconButton(onClick = { onChangeAmount(false) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                IconButton(onClick = { onChangeAmount(true) }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
    }


}


@Composable
fun EmptyMealContent(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 200.dp),
            text = stringResource(R.string.food_no_portions),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(text = stringResource(R.string.food_add))
        }
    }

}

@Composable
fun InputDialog(
    title: String,
    placeholder: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    offset: Dp,
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier.offset(x = offset),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
                Text(text = "Ok")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Card {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(text = placeholder) },
                    singleLine = true,
                    maxLines = 1,
                    keyboardActions = KeyboardActions(onDone = { onConfirm(name) }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }
    )

}


@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    offset: Dp,
) {
    AlertDialog(
        modifier = Modifier.offset(x = offset),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(text = stringResource(R.string.delete))
            }
        },
        title = {
            Text(text = stringResource(R.string.delete))
        },
        text = {
            Text(stringResource(R.string.delete_confirm_text))
        }
    )

}










