package com.xcvi.micros.ui.screens.dashboard.comp



import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.ui.core.comp.HorizontalFadedBox
import com.xcvi.micros.ui.core.comp.M3Card
import com.xcvi.micros.ui.theme.carbsDark
import com.xcvi.micros.ui.theme.carbsLight
import com.xcvi.micros.ui.theme.fatsDark
import com.xcvi.micros.ui.theme.fatsLight
import com.xcvi.micros.ui.theme.proteinDark
import com.xcvi.micros.ui.theme.proteinLight

@Composable
fun StatsCard(
    onClick: () -> Unit = {},
) {
    val isDark = isSystemInDarkTheme()
    val colorsDark = listOf(proteinDark, carbsDark, fatsDark)
    val colorsLight = listOf(proteinLight, carbsLight, fatsLight)
    Box(
        modifier = Modifier.padding(4.dp)
    ) {
        M3Card(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,

            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
            headline = {
                Text(text = stringResource(R.string.trends))
            },
            subhead = {
                Text(
                    text = stringResource(R.string.trends_subhead),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                )
            },
            body = {

            },
            media = {
                val series = listOf(
                    listOf(200.0, 184.5, 193.0, 166.0, 195.0, 185.0, 250.2), // e.g. Protein
                    listOf(151.5, 170.0, 142.5, 175.0, 240.0, 260.0, 194.0), // e.g. Carbs
                    listOf(55.0, 39.5, 64.0, 133.0, 152.5, 186.2, 198.2)  // e.g. Fat
                )
                HorizontalFadedBox(
                    modifier = Modifier.fillMaxWidth(),
                    targetColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    height = 100.dp,
                    horizontalFade = 2.dp,
                ) {
                    MultiSmoothLineGraph(
                        series = series,
                        colors = if (isDark) colorsDark else colorsLight,
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            }
        )
    }
}