package com.xcvi.micros.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.xcvi.micros.data.source.remote.ProductApi
import com.xcvi.micros.data.source.remote.dto.SearchProductDTO
import com.xcvi.micros.licence.*
import com.xcvi.micros.licence.PlayLicenseVerifier
import com.xcvi.micros.ui.navigation.AppContent
import com.xcvi.micros.ui.screens.meal.MealScreen
import com.xcvi.micros.ui.screens.message.MessageScreen
import com.xcvi.micros.ui.screens.message.MessageViewModel
import com.xcvi.micros.ui.screens.stats.StatsScreen
import com.xcvi.micros.ui.screens.stats.StatsViewModel
import com.xcvi.micros.ui.screens.weight.WeightScreen
import com.xcvi.micros.ui.screens.weight.WeightViewModel
import com.xcvi.micros.ui.theme.MicrosAITheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

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
                    AppContent()
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
















