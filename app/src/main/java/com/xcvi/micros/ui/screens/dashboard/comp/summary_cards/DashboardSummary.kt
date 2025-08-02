package com.xcvi.micros.ui.screens.dashboard.comp.summary_cards

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.ui.screens.dashboard.comp.SwipableCardStack


@Composable
fun DashboardSummary(
    modifier: Modifier = Modifier,
    context: Context,
    nutrients: Nutrients,
    minerals: Minerals,
    vitamins: Vitamins,
    aminoAcids: AminoAcids
) {
    val cardsHeights = listOf(
        260.dp, 370.dp, 390.dp, 340.dp
    )

    val currentIndex = remember { mutableIntStateOf(0) }

    Column(modifier = modifier) {
        SwipableCardStack(
            currentIndex = currentIndex.intValue,
            onIndexChange = { currentIndex.intValue = it },
            cardCount = cardsHeights.size,
            expandedHeightFor = { cardsHeights[it] }
        ) { index, expanded, height ->
            when (index) {
                0 -> MacrosSummaryCard(nutrients, expanded, context, height)
                1 -> MineralsSummaryCard(minerals, expanded, context, height)
                2 -> VitaminsSummaryCard(vitamins, expanded, context, height)
                3 -> AminosSummaryCard(aminoAcids, expanded, context, height)
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(4) { i ->
                val animatedColor by animateColorAsState(
                    targetValue = if (currentIndex.intValue == i)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                val animatedSize by animateDpAsState(
                    targetValue = if (currentIndex.intValue == i)
                        8.dp
                    else
                        6.dp
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(animatedSize)
                        .clip(CircleShape)
                        .background(animatedColor)
                        .clickable {
                            currentIndex.intValue = i
                        }
                )
            }
        }
    }
}


