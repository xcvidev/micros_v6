package com.xcvi.micros.ui.screens.dashboard.comp.summary_cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NutrientLabel(
    label: String,
    amount: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.weight(1f))

        val (fontWeight,color) =if(amount.isBlank() || amount.startsWith("0 ") || amount.startsWith("0.0 ")){
            Pair(FontWeight.Normal,MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        } else {
            Pair(FontWeight.Medium,MaterialTheme.colorScheme.onSurface)
        }
        Text(
            text = amount,
            maxLines = 1,
            fontWeight = fontWeight,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = color
        )
    }
}
