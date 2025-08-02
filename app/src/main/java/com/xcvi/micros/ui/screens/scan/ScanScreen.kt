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
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = koinViewModel(),
    onBack: () -> Unit,
    onReset: () -> Unit,
    onSuccess: (String) -> Unit,
) {
    val state = viewModel.state
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    val scanFailureMessage: String = stringResource(R.string.product_not_found)
    val scanHintText : String= stringResource(R.string.scan_barcode)
    val allowButtonText : String= stringResource(R.string.allow)
    val cancelButtonText : String= stringResource(R.string.cancel)
    val permissionDialogTitle: String = stringResource(R.string.permission_required_title)
    val permissionDialogText: String = stringResource(R.string.permission_required_text)
    val permissionDeniedText : String= stringResource(R.string.camera_permission_denied_text)
    val openSettingsButtonText : String= stringResource(R.string.open_settings)
    val failureDialogText : String= stringResource(R.string.retry_dialog_text)
    val retryButtonText : String= stringResource(R.string.retry)

    BackHandler {
        onBack()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onBack()
            },
            title = {
                Text(scanFailureMessage)
            },
            text = { Text(failureDialogText) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onReset()
                    }
                ) {
                    Text(retryButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onBack()
                    }
                ) {
                    Text(cancelButtonText)
                }
            }
        )
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            if (state.isLoading) {
                Box(
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
            } else {

                MaterialCameraScreen(
                    context = context,
                    onScan = { barcode, barcodeScanner ->
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        barcodeScanner?.close()
                        viewModel.cacheScan(
                            barcode = barcode,
                            onSuccess = { onSuccess(barcode) },
                            onFailure = {
                                barcodeScanner?.close()
                                showDialog = true
                            }
                        )
                    },
                    onGoBack = { onBack() },
                    scanHintText = scanHintText,
                    allowButtonText = allowButtonText,
                    cancelButtonText = cancelButtonText,
                    permissionDialogTitle = permissionDialogTitle,
                    permissionDialogText = permissionDialogText,
                    permissionDeniedText = permissionDeniedText,
                    openSettingsButtonText = openSettingsButtonText

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
            }
        }

    }

}



