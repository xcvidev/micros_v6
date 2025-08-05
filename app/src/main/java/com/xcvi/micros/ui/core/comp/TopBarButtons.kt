package com.xcvi.micros.ui.core.comp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BackButton(modifier: Modifier = Modifier,onClick: () -> Unit) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = ""
        )
    }
}
@Composable
fun ActionTextButton(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick
    ) {
        Text(text)
    }
}
