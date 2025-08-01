package com.xcvi.micros.ui.navigation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.licence.LicenseState
import com.xcvi.micros.licence.PlayLicenseVerifier

@Composable
fun LicenseGate(
    verifier: PlayLicenseVerifier,
    onOpenStore: () -> Unit,
    contentWhenLicensed: @Composable () -> Unit
) {
    var state by remember { mutableStateOf<LicenseState?>(null) }
    val latestStateSetter by rememberUpdatedState(newValue = { s: LicenseState -> state = s })

    // Run the check once when entering this composable
    LaunchedEffect(Unit) {
        verifier.check { latestStateSetter(it) }
    }

    when (val s = state) {
        null -> { LicenseLoadingScreen() }
        is LicenseState.Licensed -> contentWhenLicensed()
        is LicenseState.NotLicensed -> {
            NotLicensedScreen(
                onOpenStore = onOpenStore,
                onRetry = { verifier.check { latestStateSetter(it) } }
            )
        }
        is LicenseState.Error -> {
            LicenseErrorScreen(
                errorCode = s.errorCode,
                onRetry = { verifier.check { latestStateSetter(it) } }
            )
        }
    }
}
@Composable
fun LicenseLoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun NotLicensedScreen(
    onOpenStore: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.not_licensed_text), textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = onOpenStore) { Text(stringResource(R.string.play_store)) }
            Spacer(Modifier.width(12.dp))
            OutlinedButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
        }
    }
}

@Composable
fun LicenseErrorScreen(
    errorCode: Int,
    onRetry: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Couldn't verify license (code $errorCode).")
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
