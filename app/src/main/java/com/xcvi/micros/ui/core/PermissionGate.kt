package com.xcvi.micros.ui.core

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xcvi.micros.R
import com.xcvi.micros.ui.core.comp.OpenAndroidSettingsButton
import com.xcvi.micros.ui.screens.scan.BarcodeAnalyzer
import com.xcvi.micros.ui.screens.scan.CameraOverlay
import com.xcvi.micros.ui.screens.scan.findActivity

@Composable
fun PermissionGate(

    content: @Composable () -> Unit,
    //onGoBack: () -> Unit
) {
    val context = LocalContext.current
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
        hasPermission -> content()

        showRationale -> {
            AlertDialog(
                onDismissRequest = {
                    //onGoBack()
                    showRationale = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showRationale = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }) { Text(stringResource(R.string.allow)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            //onGoBack()
                            showRationale = false
                        }
                    ) { Text(stringResource(R.string.cancel)) }
                },
                title = { Text(stringResource(R.string.permission_required_title)) },
                text = { Text(stringResource(R.string.permission_required_text)) }
            )
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(500.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                ) {
                    Text(
                        text = stringResource(R.string.camera_permission_denied_text),
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                    OpenAndroidSettingsButton(
                        buttonText = stringResource(R.string.open_settings),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            }
        }
    }
}