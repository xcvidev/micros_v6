package com.xcvi.micros.ui.screens.meal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Meal
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.utils.roundDecimals
import com.xcvi.micros.ui.core.utils.toLabeledPairs
import com.xcvi.micros.ui.theme.proteinLight
import kotlinx.coroutines.flow.Flow

@Composable
fun MealSummaryCard(
    nutrients: Nutrients,
    minerals: Minerals,
    vitamins: Vitamins,
    aminoAcids: AminoAcids,
    modifier: Modifier = Modifier
) {
    val headline = "${nutrients.calories} kcal"
    val protein = "${stringResource(R.string.protein)}: ${nutrients.protein.roundDecimals()} g"
    val carbs = "${stringResource(R.string.carbs)}: ${nutrients.carbohydrates.roundDecimals()} g"
    val fats = "${stringResource(R.string.fats)}: ${nutrients.fats.roundDecimals()} g"
    val subhead = "$protein, $carbs, $fats"

    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val nutrientsLabeled = nutrients.toLabeledPairs(context)
    val mineralsLabeled = minerals.toLabeledPairs(context)
    val vitaminsLabeled = vitamins.toLabeledPairs(context)
    val aminoAcidsLabeled = aminoAcids.toLabeledPairs(context)

    val labels = nutrientsLabeled + mineralsLabeled + vitaminsLabeled + aminoAcidsLabeled

    val alpha by animateFloatAsState(
        targetValue = if (expanded) 0f else 1f,
        label = "",
        animationSpec = tween(300)
    )

    Card(
        onClick = { expanded = !expanded },
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = headline,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 4.dp)

                    )
                    Text(
                        text = subhead,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha),
                )
            }
        }
        AnimatedVisibility(expanded, exit = fadeOut() + shrinkVertically()) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 16.dp),
            ) {
                fun alpha(isEmpty: Boolean): Float = if (isEmpty) {
                    0.4f
                } else {
                    1f
                }

                labels.forEachIndexed { index: Int, value: Pair<String, String> ->
                    if (index > 3) {
                        val data = labels[index]
                        Row {
                            Text(
                                text = data.first,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha(
                                        data.second.startsWith("0.0") || data.second.startsWith("0,0")
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = data.second,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha(
                                        data.second.startsWith("0.0") || data.second.startsWith("0,0")
                                    )
                                )
                            )
                        }
                        if (index < labels.size - 1) {
                            HorizontalDivider(
                                thickness = 0.3.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f)
                            )
                        }
                    }
                }

            }
        }
    }
}



