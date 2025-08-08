package com.xcvi.micros.ui.core.comp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CheckIconButton(
    modifier: Modifier = Modifier,
    selected: Boolean, onSelect: () -> Unit) {
    OutlinedIconButton(
        modifier = modifier,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(0.6f)
            }
        ),
        onClick = {
            onSelect()
        },
        colors = if (selected) {
            IconButtonDefaults.outlinedIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
            )
        } else {
            IconButtonDefaults.outlinedIconButtonColors()
        }

    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "",
            modifier = Modifier.size(18.dp)
        )
    }
}