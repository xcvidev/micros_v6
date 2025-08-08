package com.xcvi.micros.ui.screens.meal

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.ui.core.DeleteDialog
import com.xcvi.micros.ui.core.EnhanceDialog
import com.xcvi.micros.ui.core.RemoveDialog
import com.xcvi.micros.ui.core.SummaryDetails
import com.xcvi.micros.ui.core.comp.HorizontalFadedBox
import com.xcvi.micros.ui.core.comp.LoadingIndicator
import com.xcvi.micros.ui.core.comp.NumberPicker
import com.xcvi.micros.ui.core.utils.disableBottomSheetDragWhenInteracting
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealItemDetailsSheet(
    isEnhancing: Boolean,
    context: Context,
    item: Portion,
    onConfirm: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    onScale: (Int) -> Unit,
    onEnhance: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, // Or true if you don’t want mid state

    )
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf(false) }

    var amount by remember { mutableIntStateOf(item.amount) }

    if (showDeleteDialog) {
        RemoveDialog (
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                onDelete()
                showDeleteDialog = false
            }
        )
    }

    if (showInputDialog) {
        EnhanceDialog(
            onDismiss = { showInputDialog = false },
            onConfirm = { input ->
                onEnhance(input)
                showInputDialog = false
            }
        )
    }

    val listState = rememberLazyListState()
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .statusBarsPadding(),
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        LaunchedEffect(listState.isScrollInProgress) {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showDeleteDialog = true }
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = stringResource(R.string.remove))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showInputDialog = true }
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
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onFavorite() }
                    ) {
                        val icon =
                            if (item.food.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = "")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = stringResource(R.string.favorite))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onConfirm() }
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = stringResource(R.string.save))
                        }
                    }
                }
            }

            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
                    text = item.food.name,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight
                )
            }

            if (isEnhancing) {
                item {
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .padding(vertical = 24.dp)
                            .heightIn(min = 500.dp)
                            .fillMaxWidth()
                    ) {
                        if (isEnhancing) {
                            LoadingIndicator()
                        }
                    }
                }
            } else {
                item {
                    HorizontalFadedBox(
                        height = 150.dp,
                        horizontalFade = 50.dp,
                        targetColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        NumberPicker(
                            modifier = Modifier.disableBottomSheetDragWhenInteracting(),
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
    }
}
