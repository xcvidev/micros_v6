package com.xcvi.micros.ui.screens.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.ui.core.comp.AutomaticSearchBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    date: Int,
    meal: Int,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    onScan: () -> Unit,
    onBack: () -> Unit
) {

    BackHandler {
        viewModel.saveSelected(date, meal)
        onBack()
    }

    LazyColumn(
        modifier = modifier.padding(36.dp)
    ) {
        item {
            Button(
                onClick = onScan
            ) {
                Text(text = stringResource(id = R.string.scan_barcode))
            }
        }
        item {
            AutomaticSearchBar(
                query = viewModel.state.query,
                onQueryChange = {viewModel.setQuery(it)},
                onAutomaticSearch = {viewModel.search() },
                placeHolder = ""
            )
        }
        items(viewModel.state.searchResults){
            Card {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row {
                        Text(
                            text = it.name,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier.weight(1f))
                        Button(
                            enabled = !viewModel.state.selected.contains(it.barcode),
                            onClick = {viewModel.add(it.barcode)}
                        ) {
                            Text(text = stringResource(id = R.string.add))
                        }
                    }
                }
            }
        }
    }

}














