package com.xcvi.micros.ui.screens.search

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.ui.core.comp.AutomaticSearchBar
import com.xcvi.micros.ui.core.DetailsSheet
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    date: Int,
    meal: Int,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    onScan: () -> Unit,
    onBack: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showSheet) {
        viewModel.state.selectedPortion?.let { portion ->
            DetailsSheet(
                sheetState = sheetState,
                portion = portion,
                onDismiss = {
                    scope.launch {
                        showSheet = false
                        sheetState.hide()
                    }
                },
                onConfirm = {
                    scope.launch {
                        viewModel.select(portion)
                        showSheet = false
                        sheetState.hide()
                    }
                },
                onEnhance = {input ->
                    viewModel.enhance(input)
                },
                onScale = { newAmount ->
                    viewModel.scale(newAmount)
                },
                isEnhancing = viewModel.state.isEnhancing
            )
        }
    }

    BackHandler {
        onBack()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.cancelSelection()
                            onBack()
                        }
                    ) { Icon(Icons.AutoMirrored.Default.ArrowBack, "") }
                },
                actions = {
                    if (viewModel.state.selected.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.eat { onBack()}
                            }
                        ) {
                            Text(text = stringResource(id = R.string.done))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Button(onClick = onScan) {
                Text(text = stringResource(id = R.string.scan_barcode))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.padding(padding)
        ) {
            item {
                AutomaticSearchBar(
                    query = viewModel.state.query,
                    onQueryChange = { viewModel.setQuery(it) },
                    onAutomaticSearch = { viewModel.search() },
                    placeHolder = ""
                )
            }
            items(viewModel.state.searchResults) {
                val selected = viewModel.state.selected.any { s -> s.food.barcode == it.barcode }
                Card(
                    onClick = {
                        viewModel.showDetails(it, date = date, meal = meal)
                        showSheet = true
                    }
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row {
                            Text(
                                text = it.name,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier.weight(1f))
                            Button(
                                enabled = !selected,
                                onClick = {
                                    val portion = Portion(
                                        amount = 100,
                                        date = date,
                                        meal = meal,
                                        food = it
                                    )
                                    viewModel.select(portion)
                                }
                            ) {
                                Text(text = stringResource(id = R.string.add))
                            }
                            Button(
                                enabled = selected,
                                onClick = {
                                    viewModel.unselect(it.barcode)
                                }
                            ) {
                                Text(text = stringResource(id = R.string.remove))
                            }
                        }
                    }
                }
            }
        }

    }


}












