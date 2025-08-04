package com.xcvi.micros.ui.screens.scan

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.ui.screens.meal.MealItemDetailsSheet
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScanScreen(
    date: Int,
    meal: Int,
    state: ScanState,
    onEvent: (ScanEvent) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onReset: () -> Unit,
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    val scanFailureMessage: String = stringResource(R.string.product_not_found)
    val cancelButtonText: String = stringResource(R.string.cancel)
    val failureDialogText: String = stringResource(R.string.retry_dialog_text)
    val retryButtonText: String = stringResource(R.string.retry)

    BackHandler {
        onBack()
    }

    when (state) {
        is ScanState.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(text = stringResource(R.string.fetching_product), color = Color.White)
            }
        }

        is ScanState.Error -> AlertDialog(
            onDismissRequest = {
                onBack()
            },
            title = {
                Text(scanFailureMessage)
            },
            text = { Text(failureDialogText) },
            confirmButton = {
                Button(
                    onClick = {
                        onReset()
                    }
                ) {
                    Text(retryButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onBack()
                    }
                ) {
                    Text(cancelButtonText)
                }
            }
        )

        else -> Box(modifier = modifier.fillMaxSize()) {
            MaterialCameraScreen(
                context = context,
                onScan = { barcode, barcodeScanner ->
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    barcodeScanner?.close()
                    onEvent(
                        ScanEvent.Scan(
                            barcode = barcode,
                            date = date,
                            meal = meal
                        )
                    )
                },
                onGoBack = { onBack() },
                height = 500.dp,
                width = 500.dp
            )

            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(4.dp),
                onClick = { onBack() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                    tint = Color.White,
                )
            }
            if (state is ScanState.Success) {
                ScanDetailsSheet(
                    isEnhancing = false,
                    context = context,
                    item = state.portion,
                    onConfirm = { onEvent(ScanEvent.Confirm{ onBack() }) },
                    onFavorite = { onEvent(ScanEvent.ToggleFavorite) },
                    onDismiss = { onReset() },
                    onScale = { onEvent(ScanEvent.Scale(it)) },
                    onEnhance = { onEvent(ScanEvent.Enhance(it)) },
                )
            }
        }
    }

}



