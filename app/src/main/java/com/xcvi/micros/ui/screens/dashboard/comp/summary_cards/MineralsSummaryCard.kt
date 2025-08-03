package com.xcvi.micros.ui.screens.dashboard.comp.summary_cards

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.core.comp.M3Card


@Composable
fun MineralsSummaryCard(
    summary: Minerals, expanded: Boolean, context: Context, height: Dp,
) {

    val angle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "",
        animationSpec = tween(300)
    )

    val labelsSmall = summary.toLabeledPairsSmall(context)
    val labelsExtended = summary.toLabeledPairs(context)
    val titleSmall = stringResource(R.string.hydration)

    val smallContent = @Composable {
        Column {
            Text(
                text = titleSmall,
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                color = MaterialTheme.colorScheme.primary

            )
            labelsSmall.forEachIndexed { index, label ->
                if (index > 0 && label.first.isNotBlank()) {
                    HorizontalDivider(
                        thickness = 0.3.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.3f)
                    )
                }
                NutrientLabel(
                    label = label.first,
                    amount = label.second
                )
            }
        }
    }
    val extendedContent = @Composable {
        Column {
            labelsExtended.forEachIndexed { index, label ->
                if (index > 0 && label.first.isNotBlank()) {
                    HorizontalDivider(
                        thickness = 0.3.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.3f)
                    )
                }
                NutrientLabel(
                    label = label.first,
                    amount = label.second
                )
            }
        }
    }
    Box {
        M3Card(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.height(height),
            headline = {
                Text(
                    text = stringResource(R.string.minerals),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
            },
            subhead = {
                if (expanded) {
                    extendedContent()
                } else {
                    smallContent()
                }
            }
        )
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.rotate(angle).align(Alignment.BottomCenter).padding(24.dp)
        )
    }
}

fun Minerals.toLabeledPairsSmall(context: Context): List<Pair<String, String>> {
    val minerals = this
    return listOf(
        context.getString(R.string.calcium) to "${minerals.calcium.roundToInt()} mg",
        context.getString(R.string.potassium) to "${minerals.potassium.roundToInt()} mg",
        context.getString(R.string.magnesium) to "${minerals.magnesium.roundToInt()} mg",
        context.getString(R.string.sodium) to "${minerals.sodium.roundToInt()} mg",
    )
}

fun Minerals.toLabeledPairs(context: Context): List<Pair<String, String>> {
    val minerals = this
    return listOf(
        context.getString(R.string.calcium) to "${minerals.calcium.roundToInt()} mg",
        context.getString(R.string.fluoride) to "${minerals.fluoride.roundToInt()} mg",
        context.getString(R.string.iodine) to "${minerals.iodine.roundToInt()} µg",
        context.getString(R.string.iron) to "${minerals.iron.roundToInt()} mg",
        context.getString(R.string.magnesium) to "${minerals.magnesium.roundToInt()} mg",
        context.getString(R.string.manganese) to "${minerals.manganese.roundToInt()} mg",
        context.getString(R.string.potassium) to "${minerals.potassium.roundToInt()} mg",
        context.getString(R.string.phosphorus) to "${minerals.phosphorus.roundToInt()} mg",
        context.getString(R.string.selenium) to "${minerals.selenium.roundToInt()} µg",
        context.getString(R.string.sodium) to "${minerals.sodium.roundToInt()} mg",
        context.getString(R.string.zinc) to "${minerals.zinc.roundToInt()} mg"
    )
}
