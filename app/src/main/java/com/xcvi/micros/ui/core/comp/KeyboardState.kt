package com.xcvi.micros.ui.core.comp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity

@Composable
fun keyboardOpenState(): State<Boolean> {
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    return remember {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }
}

/*
val keyboardOpen by keyboardOpenState()

 */