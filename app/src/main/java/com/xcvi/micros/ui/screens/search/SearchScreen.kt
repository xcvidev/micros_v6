package com.xcvi.micros.ui.screens.search

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.ui.core.comp.ActionTextButton
import com.xcvi.micros.ui.core.comp.AutomaticSearchBar
import com.xcvi.micros.ui.core.comp.BackButton
import com.xcvi.micros.ui.core.comp.CheckIconButton
import com.xcvi.micros.ui.core.comp.NumberPicker
import com.xcvi.micros.ui.core.comp.rememberShakeOffset
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    date: Int,
    meal: Int,
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
    onScan: () -> Unit,
    onBack: () -> Unit
) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false
    }

    LaunchedEffect(listState.isScrollInProgress) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { BackButton { onBack() } },
                actions = {
                    if (state.selectedItems.isNotEmpty()) {
                        ActionTextButton(stringResource(R.string.done)) {
                            onEvent(SearchEvent.Confirm(date=date,meal=meal) { onBack() })
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.scan_barcode)) },
                icon = { Icon(painterResource(R.drawable.ic_scan), contentDescription = null) },
                onClick = onScan,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.offset(x = shakeOffset),
            contentPadding = padding,
            state = listState
        ) {
            item {
                val errorText = stringResource(R.string.network_error)
                AutomaticSearchBar(
                    modifier = modifier.padding(16.dp),
                    query = state.query,
                    onQueryChange = { onEvent(SearchEvent.Input(it)) },
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    onAutomaticSearch = {
                        onEvent(
                            SearchEvent.Search(date = date, meal = meal) {
                                shakeTrigger = true
                                Toast.makeText(context, errorText, Toast.LENGTH_LONG)
                                    .show()
                            }
                        )
                    }
                )
            }
            item {
                Text(
                    text = state.listLabel,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            items(state.searchResults) { result ->
                val checked = state.selectedItems.any { it.food.barcode == result.food.barcode }
                FoodItem(
                    selected = checked,
                    onSelect = { onEvent(SearchEvent.Select(it)) },
                    portion = result,
                    onClick = {
                        onEvent(SearchEvent.OpenDetails(result))
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    if (state.selected != null) {
        DetailsSheet(
            item = state.selected,
            onDismiss = { onEvent(SearchEvent.CloseDetails) },
            onScale = { onEvent(SearchEvent.Scale(it)) },
            onEnhance = { onEvent(SearchEvent.Enhance(it)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsSheet(
    item: Portion,
    onDismiss: () -> Unit,
    onScale: (Int) -> Unit,
    onEnhance: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var amount by remember { mutableIntStateOf(item.amount) }
    var input by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {

        NumberPicker(
            initialValue = amount,
            onValueChange = {
                amount = it
                onScale(it)
            },
            onImeAction = {

            }
        )

        Button(
            onClick = {
                scope.launch {
                    onScale(amount)
                }
            }
        ) {
            Text("Scale")
        }
        Button(
            onClick = {
                onEnhance(input)
            }
        ) {
            Text("Enhance")
        }
        Text(item.toString())
    }


}

@Composable
fun FoodItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onSelect: (Portion) -> Unit,
    portion: Portion,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.clickable { onClick() }.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FoodItemIcon(modifier = Modifier.padding(horizontal = 8.dp), portion = portion)
                Text(
                    text = portion.food.name,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.height(12.dp).width(28.dp))
                Text(
                    text = "${portion.food.nutrients.calories} kcal, ${portion.amount} g",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))
        CheckIconButton(
            modifier = Modifier.size(28.dp),
            selected = selected,
        ) { onSelect(portion) }
        Spacer(modifier = Modifier.width(16.dp))
    }
}


@Composable
fun FoodItemIcon(modifier: Modifier= Modifier, portion: Portion?) {
    Box(modifier = modifier){
        if (portion?.food?.isFavorite == true) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "",
                modifier = Modifier.size(12.dp)
            )
        } else if (portion?.food?.isAI == true) {
            Icon(
                painter = painterResource(R.drawable.ic_gpt),
                contentDescription = "",
                modifier = Modifier.size(12.dp)
            )
        } else {
            Box(modifier = Modifier.size(12.dp))
        }
    }
}








