package com.xcvi.micros.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.xcvi.micros.licence.*
import com.xcvi.micros.licence.PlayLicenseVerifier
import com.xcvi.micros.ui.theme.MicrosAITheme

class MainActivity : ComponentActivity() {

    private lateinit var verifier: PlayLicenseVerifier
    private val base64PublicKey = PUBKEY_1 + PUBKEY_2 + PUBKEY_3

    override fun onCreate(savedInstanceState: Bundle?) {

        /*
       verifier = PlayLicenseVerifier(
           context = applicationContext,
           base64PublicKey = base64PublicKey,
           salt = LICENSE_SALT,
           installIdProvider = { InstallIdProvider.get() }
       )
       */

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MicrosAITheme {
                Surface(Modifier.fillMaxSize()) {
                    /*
                    LicenseGate(
                        verifier = verifier,
                        onOpenStore = { openPlayStore() }
                    ) {
                        AppContent()
                    }
                    */
                }
            }
        }
    }
    private fun openPlayStore() {
        val uri = "market://details?id=$packageName".toUri()
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: Exception) {
            // Fallback to web
            val web = "https://play.google.com/store/apps/details?id=$packageName".toUri()
            startActivity(Intent(Intent.ACTION_VIEW, web))
        }
    }

    override fun onDestroy() {
        if (::verifier.isInitialized) verifier.destroy()
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MicrosAITheme {
        Greeting("Android")
    }
}