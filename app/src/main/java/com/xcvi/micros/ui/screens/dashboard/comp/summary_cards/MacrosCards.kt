package com.xcvi.micros.ui.screens.dashboard.comp.summary_cards

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Macros
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.utils.roundDecimals
import com.xcvi.micros.ui.core.comp.M3Card
import com.xcvi.micros.ui.screens.dashboard.comp.MacroBar
import com.xcvi.micros.ui.screens.dashboard.comp.MacroDetails


@Composable
fun MacrosSummaryCard(
    summary: Nutrients, expanded: Boolean, context: Context, height: Dp,
) {
    val angle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "",
        animationSpec = tween(300)
    )
    val alpha by animateFloatAsState(
        targetValue = if (expanded) 0f else 1f,
        label = "",
        animationSpec = tween(300)
    )

    Box{
        M3Card(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.height(height),
            headline = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${summary.calories} kcal",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha),
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            subhead = {
                val labelsExtended = summary.toSummary(context)
                if (expanded) {
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
                } else {
                    MacroDetails(
                        Macros(calories = summary.calories, carbohydrates = summary.carbohydrates, protein = summary.protein, fats = summary.fats)
                    )
                }
            },
            media = if (!expanded) {
                { MacroBar(Macros(calories = summary.calories, carbohydrates = summary.carbohydrates, protein = summary.protein, fats = summary.fats)) }
            } else {
                null
            },
            mediaAlignment = Alignment.Center,
        )
        if(expanded){
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.rotate(angle).align(Alignment.BottomCenter).padding(24.dp)
            )
        }
    }
}


fun Nutrients.toSummary(context: Context): List<Pair<String, String>> {
    return listOf(
        context.getString(R.string.protein) to "${protein.roundDecimals()} g",
        context.getString(R.string.carbs) to "${carbohydrates.roundDecimals()} g",
        context.getString(R.string.fats) to "${fats.roundDecimals()} g",
        context.getString(R.string.saturated_fats) to "${saturatedFats.roundDecimals()} g",
        context.getString(R.string.fiber) to "${fiber.roundDecimals()} g",
        context.getString(R.string.sugars) to "${sugars.roundDecimals()} g",
    )
}
