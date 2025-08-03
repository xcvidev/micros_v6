package com.xcvi.micros.ui.screens.message
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xcvi.micros.R
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.ui.core.comp.AnimatedDots
import com.xcvi.micros.ui.core.comp.FadingText
import com.xcvi.micros.ui.core.comp.StreamingText
import com.xcvi.micros.ui.core.comp.rememberShakeOffset

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MessageScreen(
    state: MessageState,
    onEvent: (MessageEvent) -> Unit,
    onAdd: (String) -> Unit,
    onScan: () -> Unit,
) {
    val placeHolders = listOf(
        stringResource(R.string.assistant_placeholder_main1),
        stringResource(R.string.assistant_placeholder_main2),
        stringResource(R.string.assistant_placeholder_main3),
        stringResource(R.string.assistant_placeholder_main4),
    )
    val mainPlaceholder = remember { placeHolders.random() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var userInput by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }
    val lazyListState = rememberLazyListState()
    LaunchedEffect(state.isLoadingMessage) {
        if (state.messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(0)
        }
    }
    LaunchedEffect(lazyListState.isScrollInProgress) {
        focusManager.clearFocus()
        keyboardController?.hide()
    }
    val lang = stringResource(R.string.app_language)
    val onSend = {
        onEvent(MessageEvent.SendMessage(userInput, lang) { shakeTrigger = true })
        userInput = ""
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    val menuOptions = mapOf(
        stringResource(R.string.assistant_clear_history_title) to { showDeleteDialog = true },
        stringResource(R.string.assistant_show_history) to { onEvent(MessageEvent.ShowHistory) }
    )

    Scaffold(
        modifier = Modifier
            .padding(bottom = 110.dp)
            .offset(x = shakeOffset)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            },
        topBar = {
            TopAppBar(
                title = { Text(text =stringResource(R.string.assistant_topbar),fontSize = 24.sp) },
                actions = {
                    MenuButton(menuItems = menuOptions, enabled = state.messageCount > 0)
                }
            )
        },
        bottomBar = {
            InputField(
                value = userInput,
                onValueChange = { userInput = it },
                onClick = { onSend() },
                onScan = { onScan() },
                placeholder = {
                    StreamingText(
                        text = stringResource(R.string.assistant_placeholder),
                        charDelayMillis = 0
                    )
                }
            )
        }
    ) {

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            reverseLayout = true
        ) {
            if (state.isLoadingMessage) {
                item {
                    AnimatedDots(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (state.error != null) {
                when (state.error) {
                    is Failure.Network -> {
                        item {
                            Text(
                                text = stringResource(R.string.assistant_error_network),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    else -> {
                        item {
                            Text(
                                text = stringResource(R.string.assistant_error_other),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

            }
            items(state.messages.size) { index ->
                val message = state.messages[index]
                if (message.fromUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .padding(start = 100.dp, end = 8.dp, top = 48.dp, bottom = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            contentAlignment = CenterEnd
                        ) {
                            Text(
                                text = "${message.text}",
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = message.text ?: "", Modifier
                                .padding(vertical = 8.dp, horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        message.foodItems.forEach { suggestion ->
                            FoodCard(
                                name = suggestion.name,
                                onClick = { onAdd(suggestion.id) },
                                calories = suggestion.nutrients.calories,
                                amount = suggestion.amount
                            )
                        }
                    }
                }
            }
            item {
                if (state.messages.isNotEmpty() && !state.endReached) {
                    Box(modifier = Modifier.fillParentMaxWidth(), contentAlignment = Center) {
                        if (state.isLoadingPage) {
                            AnimatedDots(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    onEvent(MessageEvent.LoadMore)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "",
                                )
                            }

                        }
                    }
                }
            }
        }

        if (state.messages.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Center
            ) {
                FadingText(
                    text = mainPlaceholder,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = MaterialTheme.typography.headlineMedium.fontWeight,
                    textAlign = TextAlign.Center,
                    animationDuration = 1000
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.assistant_clear_history_title)) },
            text = { Text(stringResource(R.string.assistant_clear_history_text)) },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(MessageEvent.ClearHistory)
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun FoodCard(
    name: String,
    amount: Int,
    calories: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(9f)) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = name,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "${calories} kcal, ${amount} g",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }


            Box(
                contentAlignment = Center,
                modifier = Modifier
                    .padding(12.dp)
                    .weight(2f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

        }
    }
}

@Composable
private fun MenuButton(
    menuItems: Map<String, () -> Unit>,
    enabled: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Menu, contentDescription = "")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.key,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    },
                    onClick = {
                        expanded = false
                        item.value()
                    },
                    enabled = enabled
                )
            }
        }
    }

}


@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    onScan: () -> Unit,
    icon: ImageVector = Icons.AutoMirrored.Filled.Send,
    placeholder: @Composable () -> Unit,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Card {
            Box {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    value = value,
                    onValueChange = {
                        onValueChange(it)
                    },
                    maxLines = 2,
                    placeholder = placeholder,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                        disabledContainerColor = Transparent,
                        focusedIndicatorColor = Transparent,
                        unfocusedIndicatorColor = Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onClick()
                        }
                    ),
                )
                Row(
                    modifier = Modifier
                        .align(BottomCenter)
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onScan
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = stringResource(R.string.scan_barcode))
                            Icon(
                                painter = painterResource(R.drawable.ic_scan),
                                contentDescription = ""
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = onClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(icon, "")
                    }
                }
            }
        }
    }
}