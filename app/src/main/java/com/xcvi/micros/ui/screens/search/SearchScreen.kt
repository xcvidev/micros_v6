package com.xcvi.micros.ui.screens.search

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.ui.core.comp.NumberPicker
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
    val state = viewModel.state

    LazyColumn {
        item {
            Button(
                onClick = { viewModel.onConfirmChecked{} },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Confirm Selected")
            }
        }
        items(state.searchResults) { result ->
            val checked = state.checkedItems.any { it.food.barcode == result.food.barcode }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onItemClicked(result) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.toggleChecked(result)
                    }
                ){
                    Icon(
                        imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        tint = if(checked)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(result.food.name)
            }
        }
    }

    if (state.selected != null) {
        BottomSheet(
            item = state.selected,
            onDismiss = { viewModel.onBottomSheetDismiss() },
            onScale = { updated -> viewModel.scale(updated) },
            onEnhance = { viewModel.enhance(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
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












