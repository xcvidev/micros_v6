package com.xcvi.micros.ui.screens.scan

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.xcvi.micros.ui.core.comp.OpenAndroidSettingsButton

fun Context.findActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    throw IllegalStateException("Activity not found")
}

@Composable
fun MaterialCameraScreen(
    openSettingsButtonText: String,
    scanHintText: String,
    allowButtonText: String,
    cancelButtonText: String,
    permissionDialogTitle: String,
    permissionDialogText: String,
    permissionDeniedText: String,
    context: Context,
    onScan: (barcode: String, barcodeScanner: BarcodeScanner?) -> Unit,
    onGoBack: () -> Unit
) {
    BackHandler { onGoBack() }

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted && !ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity(), Manifest.permission.CAMERA
            )
        ) {
            showRationale = false
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity(), Manifest.permission.CAMERA
            )
            if (shouldShow) {
                showRationale = true
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }



    when {
        hasPermission -> {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val preview = Preview.Builder().build()
                        val selector = CameraSelector.DEFAULT_BACK_CAMERA

                        preview.setSurfaceProvider(previewView.surfaceProvider)

                        val imageAnalysis = ImageAnalysis.Builder().build()
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(ctx),
                            BarcodeAnalyzer { barcode, scanner -> onScan(barcode, scanner) }
                        )

                        runCatching {
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )
                        }.onFailure {
                            Log.e("CameraScreen", "Camera bind failed: ${it.localizedMessage}", it)
                        }

                        previewView
                    }
                )

                // Overlay UI
                CameraOverlay(scanHintText)

            }
        }

        showRationale -> {
            AlertDialog(
                onDismissRequest = onGoBack,
                confirmButton = {
                    TextButton(onClick = {
                        showRationale = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }) { Text(allowButtonText) }
                },
                dismissButton = {
                    TextButton(onClick = onGoBack) { Text(cancelButtonText) }
                },
                title = { Text(permissionDialogTitle) },
                text = { Text(permissionDialogText) }
            )
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = permissionDeniedText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    )
                    OpenAndroidSettingsButton(
                        buttonText = openSettingsButtonText,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            }
        }
    }
}

/*

@Composable
fun CameraOverlayFullScreen(
    scanHintText: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 100.dp),
        contentAlignment = Alignment.Center
    ) {
        val frameWidth = 300.dp
        val frameHeight = 180.dp
        val cornerLength = 30.dp
        val strokeWidth = 4.dp
        val cornerColor = Color.White.copy(alpha = 0.9f)

        val transition = rememberInfiniteTransition()
        val scanLineProgress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Canvas(modifier = Modifier.size(frameWidth, frameHeight)) {
                val strokePx = strokeWidth.toPx()
                val cornerPx = cornerLength.toPx()
                val width = size.width
                val height = size.height


                // Top-left corner
                drawLine(
                    color = cornerColor,
                    start = Offset(0f, 0f),
                    end = Offset(cornerPx, 0f),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = cornerColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, cornerPx),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )

                // Top-right corner
                drawLine(
                    color = cornerColor,
                    start = Offset(width - cornerPx, 0f),
                    end = Offset(width, 0f),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = cornerColor,
                    start = Offset(width, 0f),
                    end = Offset(width, cornerPx),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )

                // Bottom-left corner
                drawLine(
                    color = cornerColor,
                    start = Offset(0f, height - cornerPx),
                    end = Offset(0f, height),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = cornerColor,
                    start = Offset(0f, height),
                    end = Offset(cornerPx, height),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )

                // Bottom-right corner
                drawLine(
                    color = cornerColor,
                    start = Offset(width - cornerPx, height),
                    end = Offset(width, height),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = cornerColor,
                    start = Offset(width, height - cornerPx),
                    end = Offset(width, height),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )

                // Animated horizontal scan line inside frame
                val y = scanLineProgress * height
                drawLine(
                    color = cornerColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            Text(
                text = scanHintText,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )
        }
    }

}

@Composable
fun CameraOverlayFullScreenWithDimmedBackground(
    scanHintText: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 100.dp),
        contentAlignment = Alignment.Center
    ) {
        val frameWidth = 300.dp
        val frameHeight = 180.dp
        val cornerLength = 30.dp
        val strokeWidth = 4.dp
        val cornerColor = Color.White.copy(alpha = 0.9f)

        val transition = rememberInfiniteTransition()
        val scanLineProgress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Use Canvas to draw the dimmed background with a cutout for scanning frame
        Canvas(modifier = Modifier.matchParentSize()) {
            val overlayColor = Color.Black.copy(alpha = 0.6f) // dimming overlay alpha

            // Full screen rectangle
            drawRect(color = overlayColor)

            // Calculate scanning frame rect in pixels
            val frameLeft = (size.width - frameWidth.toPx()) / 2f
            val frameTop = (size.height - frameHeight.toPx()) / 2f
            val frameRight = frameLeft + frameWidth.toPx()
            val frameBottom = frameTop + frameHeight.toPx()

            // Create a path for scanning frame rectangle (to clear hole)

            val path = Path().apply {
                addRect(Rect(left = 10f, top = 10f, right = 100f, bottom = 100f))
            }

            // Clear the scanning frame area — punch a transparent hole
            drawPath(
                path = path,
                color = Color.Transparent,
                blendMode = BlendMode.Clear
            )
        }

        // Draw corners and scan line on top of the transparent hole
        Canvas(modifier = Modifier.size(frameWidth, frameHeight)) {
            val strokePx = strokeWidth.toPx()
            val cornerPx = cornerLength.toPx()
            val width = size.width
            val height = size.height

            // Draw corners (same as your existing code)
            // Top-left
            drawLine(cornerColor, Offset(0f, 0f), Offset(cornerPx, 0f), strokePx, StrokeCap.Round)
            drawLine(cornerColor, Offset(0f, 0f), Offset(0f, cornerPx), strokePx, StrokeCap.Round)

            // Top-right
            drawLine(
                cornerColor,
                Offset(width - cornerPx, 0f),
                Offset(width, 0f),
                strokePx,
                StrokeCap.Round
            )
            drawLine(
                cornerColor,
                Offset(width, 0f),
                Offset(width, cornerPx),
                strokePx,
                StrokeCap.Round
            )

            // Bottom-left
            drawLine(
                cornerColor,
                Offset(0f, height - cornerPx),
                Offset(0f, height),
                strokePx,
                StrokeCap.Round
            )
            drawLine(
                cornerColor,
                Offset(0f, height),
                Offset(cornerPx, height),
                strokePx,
                StrokeCap.Round
            )

            // Bottom-right
            drawLine(
                cornerColor,
                Offset(width - cornerPx, height),
                Offset(width, height),
                strokePx,
                StrokeCap.Round
            )
            drawLine(
                cornerColor,
                Offset(width, height - cornerPx),
                Offset(width, height),
                strokePx,
                StrokeCap.Round
            )

            // Animated horizontal scan line inside frame
            val y = scanLineProgress * height
            drawLine(cornerColor, Offset(0f, y), Offset(width, y), 2.dp.toPx(), StrokeCap.Round)
        }

        // Optional hint text below frame
        Text(
            text = scanHintText,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (frameHeight / 2) + 24.dp),
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }


}
*/