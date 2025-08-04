package com.xcvi.micros.ui.core

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.ui.core.SummaryDetails
import com.xcvi.micros.ui.core.comp.LoadingIndicator
import com.xcvi.micros.ui.core.comp.NumberPicker
import kotlinx.coroutines.launch


@Composable
fun EnhanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var userInput by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(userInput) }) {
                Text(text = "OK")
            }
        },
        title = {
            Text(text = stringResource(R.string.enhance_confirm))
        },
        text = {
            Card {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text(text = stringResource(R.string.description)) },
                    singleLine = true,
                    maxLines = 1,
                    keyboardActions = KeyboardActions(onDone = { onConfirm(userInput) }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }
    )

}

@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(text = stringResource(R.string.delete))
            }
        },
        title = {
            Text(text = stringResource(R.string.delete))
        },
        text = {
            Text(stringResource(R.string.delete_confirm_text))
        }
    )

}