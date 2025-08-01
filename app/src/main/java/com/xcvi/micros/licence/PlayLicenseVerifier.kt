package com.xcvi.micros.licence

import android.content.Context
import com.google.android.vending.licensing.AESObfuscator
import com.google.android.vending.licensing.LicenseChecker
import com.google.android.vending.licensing.LicenseCheckerCallback
import com.google.android.vending.licensing.ServerManagedPolicy
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


/**
 * Lightweight wrapper around Google Play LVL.
 *
 * @param context Android context (Application or Activity). Not retained beyond internal objects.
 * @param base64PublicKey Your Play Console Base64 public key (you can split and join it outside).
 * @param salt 20-byte (or similar) random constant for this app; used by AESObfuscator.
 * @param installIdProvider function returning a stable per-install ID (your InstallIdProvider.get()).
 */
class PlayLicenseVerifier(
    context: Context,
    base64PublicKey: String,
    salt: ByteArray,
    installIdProvider: () -> String = { InstallIdProvider.get() }
) {

    private val policy = ServerManagedPolicy(
        context,
        AESObfuscator(
            salt,
            context.packageName,
            installIdProvider() // no device identifier, uses your per‑install ID
        )
    )

    private val checker = LicenseChecker(
        context,
        policy,
        base64PublicKey
    )

    /** Callback-style API */
    fun check(onResult: (LicenseState) -> Unit) {
        checker.checkAccess(object : LicenseCheckerCallback {
            override fun allow(reason: Int) {
                onResult(LicenseState.Licensed)
            }
            override fun dontAllow(reason: Int) {
                onResult(LicenseState.NotLicensed(reason))
            }
            override fun applicationError(errorCode: Int) {
                onResult(LicenseState.Error(errorCode))
            }
        })
    }

    /** Suspend API (uses the same callback under the hood) */
    suspend fun checkSuspend(): LicenseState = suspendCancellableCoroutine { cont ->
        check { state ->
            if (cont.isActive) cont.resume(state)
        }
        // LVL doesn’t expose a cancel; we just ignore result if coroutine was cancelled.
    }

    /** Call from onDestroy() of the hosting component to unbind LVL service. */
    fun destroy() = checker.onDestroy()
}