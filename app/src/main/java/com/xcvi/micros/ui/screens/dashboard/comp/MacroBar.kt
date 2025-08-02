package com.xcvi.micros.ui.screens.dashboard.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Macros
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.theme.carbsDark
import com.xcvi.micros.ui.theme.carbsLight
import com.xcvi.micros.ui.theme.fatsDark
import com.xcvi.micros.ui.theme.fatsLight
import com.xcvi.micros.ui.theme.proteinDark
import com.xcvi.micros.ui.theme.proteinLight

fun macroCalculator(protein: Int, carbs: Int, fats: Int): Triple<Float, Float, Float> {
    val total = protein*4 + carbs*4 + fats*9
    val safeTotal = if (total <= 0f) 1f else total.toFloat()

    val proteinP = protein.toFloat()*4 / safeTotal
    val carbsP = carbs.toFloat()*4 / safeTotal
    val fatsP = fats.toFloat()*9 / safeTotal

    val proteinPercent = if (proteinP > 1f) 1f else if (proteinP <= 0f) 0.001f else proteinP
    val carbsPercent = if (carbsP > 1f) 1f else if (carbsP <= 0f) 0.001f else carbsP
    val fatsPercent = if (fatsP > 1f) 1f else if (fatsP <= 0f) 0.001f else fatsP
    return Triple(proteinPercent, carbsPercent, fatsPercent)
}

@Composable
fun MacroDetails(
    summary: Macros,
    modifier: Modifier = Modifier,
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(modifier = modifier){
        MacroLabel(
            color = if (isDarkTheme) proteinDark else proteinLight,
            label = stringResource(R.string.protein),
            amount = summary.protein.roundToInt()
        )
        MacroLabel(
            color = if (isDarkTheme) carbsDark else carbsLight,
            label = stringResource(R.string.carbs),
            amount = summary.carbohydrates.roundToInt()
        )
        MacroLabel(
            color = if (isDarkTheme) fatsDark else fatsLight,
            label = stringResource(R.string.fats),
            amount = summary.fats.roundToInt()
        )
    }
}

@Composable
fun MacroBar(
    summary: Macros,
    modifier: Modifier = Modifier,
    barHeight: Dp = 8.dp,
) {

    val protein = summary.protein
    val carbs = summary.carbohydrates
    val fats = summary.fats

    val isDark = isSystemInDarkTheme()
    val (proteinPercent, carbsPercent, fatsPercent) = macroCalculator(protein.roundToInt(), carbs.roundToInt(), fats.roundToInt())
    val proteinColor = if (isDark) proteinDark else proteinLight
    val carbsColor = if (isDark) carbsDark else carbsLight
    val fatsColor = if (isDark) fatsDark else fatsLight

    Column(
        modifier = modifier,
    ) {
        // Bar
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .fillMaxWidth()
                .height(barHeight)
        ) {

                Box(
                    modifier = Modifier
                        .height(barHeight)
                        .weight(proteinPercent)
                        .background(proteinColor)
                )

                Box(
                    modifier = Modifier
                        .height(barHeight)
                        .weight(carbsPercent)
                        .background(carbsColor)
                )

                Box(
                    modifier = Modifier
                        .height(barHeight)
                        .weight(fatsPercent)
                        .background(fatsColor)
                )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight + 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .weight(proteinPercent)
            ) {
                Text(
                    text = "${(proteinPercent * 100).roundToInt()}%",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = proteinColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(carbsPercent)
            ) {
                Text(
                    text = "${(carbsPercent * 100).roundToInt()}%",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = carbsColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(fatsPercent)
            ) {
                Text(
                    text = "${(fatsPercent * 100).roundToInt()}%",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = fatsColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}




@Composable
fun FoodSummaryCard(
    summary: Macros,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
) {
    val isDarkTheme = isSystemInDarkTheme()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(1.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MacroLabel(
                    color = if (isDarkTheme) proteinDark else proteinLight,
                    label = stringResource(R.string.protein),
                    amount = summary.protein.roundToInt()
                )
                MacroLabel(
                    color = if (isDarkTheme) carbsDark else carbsLight,
                    label = stringResource(R.string.carbs),
                    amount = summary.carbohydrates.roundToInt()
                )
                MacroLabel(
                    color = if (isDarkTheme) fatsDark else fatsLight,
                    label = stringResource(R.string.fats),
                    amount = summary.fats.roundToInt()
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${summary.calories}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "kcal",
                    color = MaterialTheme.colorScheme.primary

                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        MacroBar(
            summary = summary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MacroLabel(
    color: Color,
    label: String,
    amount: Int,
    showAmount: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(6.dp)
                    .background(color)

            )
            Text(
                text = label,
               fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
        if(showAmount){
            Text(
                text = "$amount g",
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }
    }
}
