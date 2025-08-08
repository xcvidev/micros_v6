package com.xcvi.micros.ui.screens.search

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.ui.core.comp.AutomaticSearchBar
import com.xcvi.micros.ui.core.comp.BackButton
import com.xcvi.micros.ui.core.comp.rememberShakeOffset
import kotlinx.coroutines.delay

sealed class ListItem {
    data class Header(val text: String) : ListItem()
    data class Item(val id: String, val item: Portion, val showDivider: Boolean) : ListItem()
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    date: Int,
    meal: Int,
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
    onBack: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    BackHandler {
        if (showSheet) {
            showSheet = false
        } else {
            if (state.selectedItems.isNotEmpty()) {
                showDiscardDialog = true
            } else {
                onBack()
            }
        }
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val errorText = stringResource(R.string.network_error)

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false
    }

    val selectedTitle = stringResource(R.string.selected_foods_section)

    LaunchedEffect(listState.isScrollInProgress) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    LaunchedEffect(Unit) {
        delay(150)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.surfaceContainer),
                title = {},
                navigationIcon = {
                    BackButton(modifier.padding(horizontal = 8.dp)) { showDiscardDialog = true }
                },
                actions = {
                    if (state.selectedItems.isNotEmpty()) {
                        TextButton(
                            modifier = modifier.padding(horizontal = 8.dp),
                            onClick = {
                                onEvent(
                                    SearchEvent.Confirm(
                                        date = date,
                                        meal = meal
                                    ) { onBack() })
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.done),
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = modifier.imePadding(),
                text = { Text(stringResource(R.string.scan_barcode)) },
                icon = { Icon(painterResource(R.drawable.ic_scan), contentDescription = null) },
                onClick = {
                    showSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            )
        },
    ) { padding ->
        LazyColumn(

            modifier = modifier
                .offset(x = shakeOffset)
                .padding(padding),
            state = listState
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    var isTyping by remember { mutableStateOf(false) }
                    AutomaticSearchBar(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .focusRequester(focusRequester),
                        query = state.query,
                        onTyping = { isTyping = true },
                        onTypingEnd = { isTyping = false },
                        onQueryChange = { onEvent(SearchEvent.Input(it)) },
                        label = { Text(text = stringResource(R.string.search_placeholder)) },
                        onAutomaticSearch = {
                            onEvent(
                                SearchEvent.Search(date = date, meal = meal) {
                                    Toast.makeText(context, errorText, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { onEvent(SearchEvent.Input("")) }) {
                                    Icon(Icons.Default.Clear, "")
                                }
                            }
                        },
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    SmartSearchButton(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        searching = state.isLoadingSmartSearch || state.isLoadingSearch || isTyping,
                    ) {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onEvent(
                            SearchEvent.SmartSearch(date = date, meal = meal) { e ->
                                shakeTrigger = true
                                if (e !is Failure.InvalidInput) {
                                    Toast.makeText(context, errorText, Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                    }
                }
            }
            val selectedItems = state.selectedItems
            val searchItems =
                state.searchResults.filter { result -> selectedItems.none { it.food.barcode == result.food.barcode } }
            val combined: List<ListItem> = buildList {
                if (selectedItems.isNotEmpty()) {
                    add(ListItem.Header(selectedTitle))
                    addAll(
                        selectedItems.map {
                            ListItem.Item(
                                "selected_${it.food.barcode}",
                                it,
                                selectedItems.indexOf(it) > 0
                            )
                        }
                    )
                }
                add(ListItem.Header(state.listLabel))
                addAll(
                    searchItems.map {
                        ListItem.Item(
                            "search_${it.food.barcode}",
                            it,
                            searchItems.indexOf(it) > 0
                        )
                    }
                )
            }

            /*
            items(
                combined,
                key = {
                    when (it) {
                        is ListItem.Header -> "header_${it.text}"
                        is ListItem.Item -> it.id
                    }
                }
            ) { entry ->
                when (entry) {
                    is ListItem.Header -> Text(
                        text = entry.text,
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier.padding(start = 24.dp, bottom = 8.dp, top = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    is ListItem.Item -> Column(
                        modifier = Modifier.animateItem(
                            fadeInSpec = null, fadeOutSpec = null, placementSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            )
                        )
                    ) {
                        if(entry.showDivider){
                            HorizontalDivider(
                                thickness = 0.3.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        FoodItem(
                            selected = entry.item.food.barcode in selectedItems.map { it.food.barcode },
                            onSelect = {
                                onEvent(SearchEvent.Select(it))
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            },
                            portion = entry.item,
                            onClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                showSheet = true
                                onEvent(SearchEvent.OpenDetails(entry.item))
                            }
                        )
                    }
                }
            }
            */


            if (state.selectedItems.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.selected_foods_section),
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier.padding(start = 24.dp, bottom = 8.dp, top = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
            items(selectedItems.toList(), key = { "selected_${it.food.barcode}" }) { result ->
                // val checked = state.selectedItems.any { it.food.barcode == result.food.barcode }
                if (selectedItems.indexOf(result) > 0) {
                    HorizontalDivider(
                        thickness = 0.3.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                FoodItem(
                    modifier = Modifier.animateItem(),
                    selected = true,
                    onSelect = {
                        onEvent(SearchEvent.Select(it))
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                    portion = result,
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        showSheet = true
                        onEvent(SearchEvent.OpenDetails(result))
                    }
                )
            }



            item {
                Text(
                    text = state.listLabel,
                    fontSize = MaterialTheme.typography.labelLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(start = 24.dp, bottom = 8.dp, top = 24.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            item {
                Column {
                    state.smartResult?.text?.let { message ->
                        Text(
                            text = message,
                            modifier = modifier.padding(
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 8.dp,
                                top = 16.dp
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            items(items = searchItems, key = { "search_${it.food.barcode}" }) { result ->
                //val checked = state.selectedItems.any { it.food.barcode == result.food.barcode }
                if (state.searchResults.indexOf(result) > 0) {
                    HorizontalDivider(
                        thickness = 0.3.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                FoodItem(
                    modifier = Modifier.animateItem(),
                    selected = false,
                    onSelect = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onEvent(SearchEvent.Select(it))
                    },
                    portion = result,
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        showSheet = true
                        onEvent(SearchEvent.OpenDetails(result))
                    }
                )
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
            SearchSheetContent(
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

    if (showDiscardDialog) {
        DiscardDialog(
            onDiscard = onBack,
            onDismiss = { showDiscardDialog = false },
            onConfirm = {
                onEvent(
                    SearchEvent.Confirm(
                        date = date,
                        meal = meal,
                        onSuccess = onBack
                    )
                )
            }
        )
    }
}


@Composable
fun DiscardDialog(
    onDismiss: () -> Unit,
    onDiscard: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel))
                }
                TextButton(onClick = onDiscard) {
                    Text(text = stringResource(R.string.discard))
                }
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(R.string.save))
                }
            }
        },
        title = {
            Text(text = stringResource(R.string.unsaved_changes_title))
        },
        text = {
            Text(stringResource(R.string.unsaved_changes_text))
        }
    )

}


@Composable
fun EmptyRecentsContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
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







