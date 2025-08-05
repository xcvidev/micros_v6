package com.xcvi.micros.ui.screens.search

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import com.xcvi.micros.ui.core.comp.AutomaticSearchBar
import com.xcvi.micros.ui.core.comp.BackButton
import com.xcvi.micros.ui.core.comp.CheckIconButton
import com.xcvi.micros.ui.core.comp.HorizontalFadedBox
import com.xcvi.micros.ui.core.comp.LoadingIndicator
import com.xcvi.micros.ui.core.comp.NumberPicker
import com.xcvi.micros.ui.core.comp.keyboardOpenState
import com.xcvi.micros.ui.core.comp.rememberShakeOffset
import com.xcvi.micros.ui.core.utils.disableBottomSheetDragWhenInteracting
import kotlinx.coroutines.delay

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

    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false
    }

    var showSheet by remember { mutableStateOf(false) }
    val keyboardOpen by keyboardOpenState()

    LaunchedEffect(listState.isScrollInProgress) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    LaunchedEffect(Unit) {
        delay(150) // wait for composition
        focusRequester.requestFocus()
        keyboardController?.show()
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
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .statusBarsPadding()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(modifier.padding(horizontal = 8.dp)) { onBack() }

                    val errorText = stringResource(R.string.network_error)
                    AutomaticSearchBar(
                        modifier = modifier.weight(1f)
                            .focusRequester(focusRequester),
                        query = state.query,
                        onQueryChange = { onEvent(SearchEvent.Input(it)) },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                //textAlign = TextAlign.Center,
                                //modifier = modifier.fillMaxWidth()
                            )
                        },
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
                        modifier = modifier.padding(horizontal = 8.dp),
                        onClick = {
                            onEvent(SearchEvent.Confirm(date = date, meal = meal) { onBack() })
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.done),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
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
                modifier = modifier.offset(x = shakeOffset),
                contentPadding = padding,
                state = listState
            ) {

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
    }
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







