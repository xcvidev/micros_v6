package com.xcvi.micros.ui.screens.search

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.xcvi.micros.R
import com.xcvi.micros.ui.core.PermissionGate
import com.xcvi.micros.ui.screens.scan.CameraOverlay
import kotlinx.coroutines.delay

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerSheet(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    state: ScannerState,
    onDismiss: () -> Unit,
    onBarcodeScanned: (String) -> Unit,
    onRetryScan: () -> Unit,
    detailsContent: @Composable () -> Unit
) {

    val sheetState = if(state == ScannerState.ShowResult){
        rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
        )
    } else {
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
    }
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(state) {
        if (state == ScannerState.ShowResult) {
            delay(150)
            sheetState.expand()
        }
    }

    if (isOpen) {
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = modifier
                .padding(top = 120.dp)
                .padding(horizontal = 8.dp),
            sheetState = sheetState,
            onDismissRequest = onDismiss,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Box(modifier = modifier.fillMaxWidth()) {
                when (state) {
                    ScannerState.Scanning -> {
                        PermissionGate {
                            BarcodeScannerUI(
                                isOpen = isOpen,
                                onScan = { scanned ->
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onBarcodeScanned(scanned)
                                }
                            )
                        }
                    }

                    ScannerState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(3f / 4f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.height(8.dp))
                                Text(text = stringResource(R.string.fetching_product))
                            }
                        }
                    }

                    ScannerState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(3f / 4f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.retry_dialog_text),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 48.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TextButton(
                                    onClick = onDismiss,
                                ) {
                                    Text(
                                        text = stringResource(R.string.cancel),
                                        modifier = modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    )
                                }
                                Button(
                                    onClick = onRetryScan
                                ) {
                                    Text(
                                        text = stringResource(R.string.retry),
                                        modifier = modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    ScannerState.ShowResult -> {
                        detailsContent()
                    }
                }
            }
        }
    }
}


@Composable
fun BarcodeScannerUI(
    isOpen: Boolean,
    onScan: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    // Camera Preview
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f) // typical camera preview ratio
            .clipToBounds()
    ) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { previewView },
        )

        CameraOverlay(stringResource(R.string.scan_barcode))
    }


    // Bind the camera when the sheet is shown
    LaunchedEffect(isOpen) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().apply {
            surfaceProvider = previewView.surfaceProvider
        }

        val analyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().apply {
                val scanner = BarcodeScanning.getClient()
                setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    processImageProxy(scanner, imageProxy) { scanned ->
                        onScan(scanned)
                    }
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analyzer
            )
        } catch (e: Exception) {
            Log.e("BarcodeScanner", "Camera binding error", e)
        }
    }

    // Unbind camera on dispose
    DisposableEffect(Unit) {
        onDispose {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image ?: return imageProxy.close()

    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.rawValue?.let { value ->
                onScanned(value)
            }
        }
        .addOnFailureListener {
            Log.e("BarcodeScanner", "Scan failed", it)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

