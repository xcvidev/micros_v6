package com.xcvi.micros.ui.screens.scan

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.ui.core.EnhanceDialog
import com.xcvi.micros.ui.core.SummaryDetails
import com.xcvi.micros.ui.core.comp.LoadingIndicator
import com.xcvi.micros.ui.core.comp.NumberPicker
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDetailsSheet(
    isEnhancing: Boolean,
    context: Context,
    item: Portion,
    onConfirm: () -> Unit,
    onFavorite: () -> Unit,
    onDismiss: () -> Unit,
    onScale: (Int) -> Unit,
    onEnhance: (String) -> Unit,
    skipToFullyExpanded: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipToFullyExpanded)
    val scope = rememberCoroutineScope()


    var showInputDialog by remember { mutableStateOf(false) }

    var amount by remember { mutableIntStateOf(item.amount) }


    if (showInputDialog) {
        EnhanceDialog(
            onDismiss = { showInputDialog = false },
            onConfirm = { input ->
                onEnhance(input)
                showInputDialog = false
            }
        )
    }

    ModalBottomSheet(
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
        LazyColumn {
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
                            Icon(imageVector = Icons.Default.Add, contentDescription = "")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = stringResource(R.string.add))
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

            item {
                NumberPicker(
                    initialValue = amount,
                    onValueChange = {
                        if (it > 0) {
                            amount = it
                            onScale(it)
                        }
                    },
                    onImeAction = {}
                )
            }

            item {
                Box(
                    contentAlignment = Alignment.Center
                ) {

                    SummaryDetails(
                        nutrients = item.food.nutrients,
                        minerals = item.food.minerals,
                        vitamins = item.food.vitamins,
                        aminoAcids = item.food.aminoAcids,
                        context = context
                    )
                    if (isEnhancing) {
                        LoadingIndicator()
                    }
                }
            }
        }
    }
}
