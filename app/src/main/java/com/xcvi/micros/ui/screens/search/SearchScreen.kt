package com.xcvi.micros.ui.screens.search

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.ui.core.EnhanceDialog
import com.xcvi.micros.ui.core.SummaryDetails
import com.xcvi.micros.ui.core.comp.ActionTextButton
import com.xcvi.micros.ui.core.comp.AutomaticSearchBar
import com.xcvi.micros.ui.core.comp.BackButton
import com.xcvi.micros.ui.core.comp.CheckIconButton
import com.xcvi.micros.ui.core.comp.LoadingIndicator
import com.xcvi.micros.ui.core.comp.NumberPicker
import com.xcvi.micros.ui.core.comp.rememberShakeOffset
import com.xcvi.micros.ui.core.utils.disableBottomSheetDragWhenInteracting
import com.xcvi.micros.ui.core.utils.getMealName

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    date: Int,
    meal: Int,
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
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

    var showSheet by remember { mutableStateOf(false) }


    LaunchedEffect(listState.isScrollInProgress) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    BackHandler {
        if (showSheet) {
            showSheet = false
        } else {
            onBack()
        }
    }

    BoxWithConstraints {
        val maxHeight = maxHeight
        val maxWidth = maxWidth
        Scaffold(
            modifier = modifier.pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
            topBar = {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton { onBack() }
                    val errorText = stringResource(R.string.network_error)
                    AutomaticSearchBar(
                        modifier = modifier.weight(1f),
                        query = state.query,
                        onQueryChange = { onEvent(SearchEvent.Input(it)) },
                        leadingIcon = { Icon(Icons.Default.Search, "") },
                        placeholder = { Text(stringResource(R.string.search_placeholder)) },
                        onAutomaticSearch = {
                            onEvent(
                                SearchEvent.Search(date = date, meal = meal) {
                                    shakeTrigger = true
                                    Toast.makeText(context, errorText, Toast.LENGTH_LONG)
                                        .show()
                                }
                            )
                        },
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    TextButton(
                        onClick = {
                            onEvent(SearchEvent.Confirm(date = date, meal = meal) { onBack() })
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            text = stringResource(R.string.done),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.scan_barcode)) },
                    icon = { Icon(painterResource(R.drawable.ic_scan), contentDescription = null) },
                    onClick = {
                        showSheet = true
                    },
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
                    Text(
                        text = state.listLabel,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = modifier.padding(horizontal = 26.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                item {
                    state.searchResults.forEachIndexed { index, result ->
                        val checked =
                            state.selectedItems.any { it.food.barcode == result.food.barcode }
                        if (index > 0) {
                            HorizontalDivider(
                                thickness = 0.3.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        FoodItem(
                            selected = checked,
                            onSelect = { onEvent(SearchEvent.Select(it)) },
                            portion = result,
                            onClick = {
                                showSheet = true
                                onEvent(SearchEvent.OpenDetails(result))
                            }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }

        BarcodeScannerSheet(
            state = state.scannerState,
            isOpen = showSheet,
            onDismiss = {
                showSheet = false
                onEvent(SearchEvent.CloseDetails)
            },
            onBarcodeScanned = { barcode ->
                onEvent(
                    SearchEvent.Scan(
                        barcode = barcode,
                        date = date,
                        meal = meal
                    )
                )
            },
            onRetryScan = {
                onEvent(SearchEvent.ResetScanner)
            },
        ) {
            if (state.selected != null) {
                val isSelected =
                    state.selectedItems.any { it.food.barcode == state.selected.food.barcode }
                SheetContent(
                    item = state.selected,
                    isEnhancing = state.isEnhancing,
                    isSelected = isSelected,
                    onDismiss = {
                        showSheet = false
                        onEvent(SearchEvent.CloseDetails)
                    },
                    onScale = { onEvent(SearchEvent.Scale(it)) },
                    onEnhance = { onEvent(SearchEvent.Enhance(it)) },
                    onFavorite = { onEvent(SearchEvent.ToggleFavorite) },
                    onSelect = { onEvent(SearchEvent.Select(state.selected)) },
                    context = context
                )
            }
        }

        if (state.recents.isEmpty() && state.searchResults.isEmpty()) {
            EmptyRecentsContent(
                modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun SheetContent(
    modifier: Modifier = Modifier,
    context: Context,
    isEnhancing: Boolean,
    isSelected: Boolean,
    onDismiss: () -> Unit,
    onScale: (Int) -> Unit,
    onEnhance: (String) -> Unit,
    onFavorite: () -> Unit,
    onSelect: () -> Unit,
    item: Portion,
) {
    var showInputDialog by remember { mutableStateOf(false) }
    var amount by remember { mutableIntStateOf(item.amount) }
    val listState = rememberLazyListState()

    if (showInputDialog) {
        EnhanceDialog(
            onDismiss = { showInputDialog = false },
            onConfirm = { input ->
                onEnhance(input)
                showInputDialog = false
            }
        )
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(listState.isScrollInProgress) {
        focusManager.clearFocus()
        keyboardController?.hide()
    }
    LazyColumn(
        state = listState,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        }
    ) {
        item {
            Column(modifier = Modifier.disableBottomSheetDragWhenInteracting()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onFavorite() },
                        contentAlignment = Alignment.Center
                    ) {
                        val icon =
                            if (item.food.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = "")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = stringResource(R.string.favorite))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showInputDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = if (item.food.isAI) {
                            painterResource(R.drawable.ic_ai_filled)
                        } else {
                            painterResource(R.drawable.ic_ai)
                        }
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = ""
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = stringResource(R.string.enhance_confirm))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (!isSelected) {
                                    onSelect()
                                }
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = if (isSelected) Icons.Default.Check else Icons.Default.Add
                        val text =
                            if (isSelected) stringResource(R.string.update) else stringResource(R.string.add)
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = "")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = text)
                        }
                    }
                }
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
                    text = item.food.name,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight
                )
                if (isEnhancing) {
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .padding(vertical = 24.dp)
                            .heightIn(min = 500.dp)
                            .fillMaxWidth()
                    ) {
                        LoadingIndicator()
                    }
                } else {
                    NumberPicker(
                        modifier = modifier.disableBottomSheetDragWhenInteracting(),
                        initialValue = amount,
                        onValueChange = {
                            if (it > 0) {
                                amount = it
                                onScale(it)
                            }
                        }
                    )
                }
            }
        }
        item {
            SummaryDetails(
                nutrients = item.food.nutrients,
                minerals = item.food.minerals,
                vitamins = item.food.vitamins,
                aminoAcids = item.food.aminoAcids,
                context = context
            )
        }
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
            modifier = Modifier
                .clickable { onClick() }
                .weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FoodItemIcon(
                    modifier = Modifier.padding(start = 8.dp, end = 6.dp),
                    portion = portion
                )
                Text(
                    text = portion.food.name,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FoodItemIcon(modifier = Modifier.padding(start = 8.dp, end = 6.dp), portion = null)
                Text(
                    text = "${portion.food.nutrients.calories} kcal • ${portion.amount} g",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))
        CheckIconButton(
            modifier = Modifier.size(28.dp),
            selected = selected,
        ) { onSelect(portion) }
        Spacer(modifier = Modifier.width(16.dp))
    }
}


@Composable
fun FoodItemIcon(modifier: Modifier = Modifier, portion: Portion?) {
    Box(modifier = modifier) {
        if (portion?.food?.isFavorite == true) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "",
                modifier = Modifier.size(12.dp)
            )
        } else if (portion?.food?.isAI == true) {
            Icon(
                painter = painterResource(R.drawable.ic_ai),
                contentDescription = "",
                modifier = Modifier.size(12.dp)
            )
        } else {
            Box(modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
fun EmptyRecentsContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            text = stringResource(R.string.empty_recents),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }
}







