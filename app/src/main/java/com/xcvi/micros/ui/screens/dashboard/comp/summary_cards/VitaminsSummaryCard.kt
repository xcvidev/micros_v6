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
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.core.comp.M3Card


@Composable
fun VitaminsSummaryCard(
    vitamins: Vitamins, expanded: Boolean, context: Context, height: Dp,
) {

    val angle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "",
        animationSpec = tween(300)
    )

    val labelsSmall = vitamins.toSummarySmall(context)
    val labelsExtended = vitamins.toSummary(context)
    val titleSmall = stringResource(R.string.totals)

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

    Box{
        M3Card(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,

            modifier = Modifier.height(height),
            headline = {
                Text(
                    text = stringResource(R.string.vitamins),
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

fun Vitamins.toSummarySmall(context: Context): List<Pair<String, String>> {
    val vitamins = this
    return listOf(
        context.getString(R.string.vitaminA) to "${vitamins.vitaminA.roundToInt()} μg",
        context.getString(R.string.vitaminB) to "${vitamins.vitaminBTotal().roundToInt()} mg",
        context.getString(R.string.vitaminC) to "${vitamins.vitaminC.roundToInt()} mg",
        context.getString(R.string.vitaminD) to "${vitamins.vitaminD.roundToInt()} μg",
    )
}

fun Vitamins.toSummary(context: Context): List<Pair<String, String>> {
    val vitamins = this
    return listOf(
        context.getString(R.string.vitaminA) to "${vitamins.vitaminA.roundToInt()} μg",
        context.getString(R.string.vitaminB1) to "${vitamins.vitaminB1.roundToInt()} mg",
        context.getString(R.string.vitaminB2) to "${vitamins.vitaminB2.roundToInt()} mg",
        context.getString(R.string.vitaminB3) to "${vitamins.vitaminB3.roundToInt()} mg",
        context.getString(R.string.vitaminB4) to "${vitamins.vitaminB4.roundToInt()} mg",
        context.getString(R.string.vitaminB5) to "${vitamins.vitaminB5.roundToInt()} mg",
        context.getString(R.string.vitaminB6) to "${vitamins.vitaminB6.roundToInt()} mg",
        context.getString(R.string.vitaminB9) to "${vitamins.vitaminB9.roundToInt()} μg",
        context.getString(R.string.vitaminB12) to "${vitamins.vitaminB12.roundToInt()} μg",
        context.getString(R.string.vitaminC) to "${vitamins.vitaminC.roundToInt()} mg",
        context.getString(R.string.vitaminD) to "${vitamins.vitaminD.roundToInt()} μg",
        context.getString(R.string.vitaminE) to "${vitamins.vitaminE.roundToInt()} mg",
        context.getString(R.string.vitaminK) to "${vitamins.vitaminK.roundToInt()} μg",
    )
}
