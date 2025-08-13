package com.xcvi.micros.ui.screens.dashboard.comp

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Meal


@Composable
fun AnimatedMealCard(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val transition = updateTransition(targetState = visible, label = "mealCardVisibility")

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "alpha"
    ) { if (it) 1f else 0f }

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "scale"
    ) { if (it) 1f else 0.4f }

    if (alpha > 0f) { // only compose when visible or animating out
        Box(
            modifier = modifier
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scale
                    this.scaleY = scale
                }
                .padding(4.dp) // spacing for lazy column
                .clip(RoundedCornerShape(16.dp))
        ) {
            content()
        }
    }
}


@Composable
fun MealCardFull(
    meal: Meal,
    modifier: Modifier = Modifier
) {
    val label = meal.name

    val alpha = if (meal.portions.isEmpty()) {
        0.6f
    } else {
        1f
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()              // Default  guideline padding
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            val calories = "${meal.nutrients.calories} kcal"
            Text(
                text = calories,
                style = MaterialTheme.typography.titleSmall,
                color = if (meal.portions.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)              // Default  guideline padding
            )

            if (meal.portions.isEmpty()) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)           // Default  guideline padding for supporting text
                )
            } else {
                Column(
                    modifier = Modifier.padding(vertical = 4.dp)              // Default  guideline padding
                ) {
                    meal.portions.forEach {
                        Text(
                            text = it.food.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MealCardEmpty(
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()              // Default  guideline padding
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp)              // Default  guideline padding for title
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)              // Default  guideline padding for subhead
                )


                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)              // Default  guideline padding for supporting text
                )


            }
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}