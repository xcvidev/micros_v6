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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
    modifier: Modifier = Modifier,
) {

    OnNavigation {
        onEvent(MealEvent.GetMeal(date = date, number = number))
    }


    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }
    val context = LocalContext.current
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
                    }
                }
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp),
                    ) {
                        state.portions.forEachIndexed { index, portion ->
                            if (index > 0) {
                                HorizontalDivider(
                                    thickness = 0.3.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            ItemCard(
                                portion = portion,
                                onClick = {
                                    onEvent(MealEvent.OpenDetails(portion))

                                },
                                onLongClick = {
                                    onEvent(MealEvent.OpenDetails(portion))
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                if (state.portions.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }


    if (state.selected != null) {
        MealItemDetailsSheet(
            context = context,
            item = state.selected,
            onConfirm = {
                onEvent(MealEvent.Confirm)
            },
            onDelete = {
                onEvent(MealEvent.DeletePortion)
            },
            onDismiss = { onEvent(MealEvent.CloseDetails) },
            onScale = { onEvent(MealEvent.Scale(it)) },
            onEnhance = { onEvent(MealEvent.Enhance(it)) },
            onFavorite = { onEvent(MealEvent.ToggleFavorite) },
            isEnhancing = state.isEnhancing
        )
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
            textAlign = TextAlign.Center,
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    portion: Portion,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)

            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = portion.food.name,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "${portion.food.nutrients.calories} kcal • ${portion.amount} g",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            /*
            // button
            Row(modifier = Modifier.padding(8.dp)) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = ""
                )
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
             */
        }
    }


}










