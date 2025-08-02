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
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.utils.roundDecimals
import com.xcvi.micros.ui.core.M3Card


@Composable
fun AminosSummaryCard(
    aminoAcids: AminoAcids, expanded: Boolean, context: Context, height: Dp,
) {

    val angle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "",
        animationSpec = tween(300)
    )
    val labelsSmall = aminoAcids.toSummarySmall(context)
    val labelsExtended = aminoAcids.toSummary(context)
    val titleSmall = stringResource(R.string.bcaa)
    val titleExtended = stringResource(R.string.essentials)

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
            Text(
                text = titleExtended,
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                color = MaterialTheme.colorScheme.primary
            )
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
                    text = stringResource(R.string.aminos),
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

fun AminoAcids.toSummarySmall(context: Context): List<Pair<String, String>> {
    val aminoAcids = this
    return listOf(
        context.getString(R.string.leucine) to "${aminoAcids.leucine.roundDecimals()} g",
        context.getString(R.string.isoleucine) to "${aminoAcids.isoleucine.roundDecimals()} g",
        context.getString(R.string.valine) to "${aminoAcids.valine.roundDecimals()} g",
        "" to "",
    )
}

fun AminoAcids.toSummary(context: Context): List<Pair<String, String>> {
    val aminoAcids = this
    return listOf(
        context.getString(R.string.histidine) to "${aminoAcids.histidine.roundDecimals()} g",
        context.getString(R.string.isoleucine) to "${aminoAcids.isoleucine.roundDecimals()} g",
        context.getString(R.string.leucine) to "${aminoAcids.leucine.roundDecimals()} g",
        context.getString(R.string.lysine) to "${aminoAcids.lysine.roundDecimals()} g",
        context.getString(R.string.methionine) to "${aminoAcids.methionine.roundDecimals()} g",
        context.getString(R.string.phenylalanine) to "${aminoAcids.phenylalanine.roundDecimals()} g",
        context.getString(R.string.threonine) to "${aminoAcids.threonine.roundDecimals()} g",
        context.getString(R.string.tryptophan) to "${aminoAcids.tryptophan.roundDecimals()} g",
        context.getString(R.string.valine) to "${aminoAcids.valine.roundDecimals()} g"
    )
}