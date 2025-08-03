package com.xcvi.micros.ui.screens.goals

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Macros
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.RdaCalculator
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.utils.roundDecimals
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.core.comp.LoadingIndicator
import com.xcvi.micros.ui.screens.dashboard.comp.ScoreBar
import com.xcvi.micros.ui.screens.dashboard.comp.macrosScoreCalculator
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
    state: GoalsState,
    onEvent: (GoalsEvent) -> Unit,
    onBack: () -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler {
        onBack()
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val height = maxHeight
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (state is GoalsState.Goals) {
                            Text(text = stringResource(R.string.goals_title))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                ""
                            )
                        }
                    }
                )
            }
        ) { padding ->
            when (state) {
                GoalsState.Loading -> LoadingIndicator(modifier.fillMaxSize())
                GoalsState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val info1 = stringResource(R.string.goals_info_1)
                        val info2a = stringResource(R.string.goals_info_2a)
                        val info2b = stringResource(R.string.goals_info_2b)
                        val info3 = stringResource(R.string.goals_info_3)
                        val ai = stringResource(R.string.ai)
                        val text = buildAnnotatedString {
                            appendLine(info1)
                            append(info2a)
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(" $ai ")
                            }
                            appendLine(info2b)
                            append(info3)
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            text = text,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                        Button(
                            onClick = { showSheet = true },
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(text = stringResource(R.string.set_goals))
                        }
                    }
                }
                is GoalsState.Goals -> {
                    val currentGoals = state.currentGoals
                    LazyColumn(
                        modifier = modifier.padding(horizontal = 24.dp),
                        contentPadding = padding,
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.current_goals),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                        item {
                            Box {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                                        val text = buildAnnotatedString {
                                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("${currentGoals.goal.calories}")
                                            }
                                            withStyle(
                                                SpanStyle(
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                                                )
                                            ) {
                                                append(" kcal")
                                            }
                                        }
                                        Text(
                                            text = text,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    val pText = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                                            append(stringResource(R.string.protein))
                                        }
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(" ${currentGoals.goal.protein} g")
                                        }
                                    }
                                    val cText = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                                            append(stringResource(R.string.carbs))
                                        }
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(" ${currentGoals.goal.carbohydrates} g")
                                        }
                                    }
                                    val fText = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                                            append(stringResource(R.string.fats))
                                        }
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(" ${currentGoals.goal.fats} g")
                                        }
                                    }

                                    Text(
                                        text = pText,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = cText,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = fText,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )


                                }
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = MaterialTheme.colorScheme.background,
                                    ),
                                    onClick = { showSheet = true },
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                ) {
                                    Text(stringResource(R.string.update))
                                }
                            }
                        }

                        item {
                            Text(
                                text = stringResource(R.string.scores),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 24.dp),
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.macros_goals),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                val macrosScore = macrosScoreCalculator(
                                    actual = state.currentGoals.actual,
                                    goal = state.currentGoals.goal
                                )
                                ScoreBar(score = macrosScore)
                                // LabelsList(labels = state.nutrients.macroGoals(context = context))

                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { }
                                    .padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.minerals),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                val mineralsRda = RdaCalculator.forMinerals()
                                val mineralsScore =
                                    mineralsScoreCalculator(
                                        state.currentGoals.actualMinerals,
                                        mineralsRda
                                    )
                                ScoreBar(score = mineralsScore)
                                LabelsList(
                                    labels = state.currentGoals.actualMinerals.rda(
                                        context = context,
                                        mineralsRda = mineralsRda
                                    )
                                )

                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        item {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.vitamins),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }

                                val vitaminsRda = RdaCalculator.forVitamins()
                                val vitaminsScore =
                                    vitaminsScoreCalculator(
                                        summary = state.currentGoals.actualVitamins,
                                        vitaminsRda = vitaminsRda
                                    )
                                ScoreBar(score = vitaminsScore)
                                LabelsList(
                                    labels = state.currentGoals.actualVitamins.rda(
                                        context = context,
                                        vitaminsRda = vitaminsRda
                                    )
                                )


                            }
                        }

                        item {
                            Text(
                                text = stringResource((R.string.rda_info)),
                                modifier = Modifier.padding(top = 24.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }

            if (showSheet) {
                EditGoalsSheet(
                    goals = when (state) {
                        is GoalsState.Goals -> state.currentGoals.goal
                        else -> Macros()
                    },
                    height = height.times(0.6f),
                    onDismiss = { showSheet = false },
                    sheetState = sheetState,
                    onConfirm = { protein, carbs, fats, onError ->
                        coroutineScope.launch {
                            sheetState.hide()
                            showSheet = false
                            onEvent(
                                GoalsEvent.SetCurrentGoals(
                                    protein = protein,
                                    carbs = carbs,
                                    fats = fats,
                                    onError = onError
                                )
                            )
                        }
                    }
                )
            }

        }
    }


}

@Composable
fun LabelsList(labels: List<Triple<String, String, String>>) {
    Column {
        labels.forEachIndexed { index, label ->
            if (index > 0 && label.first.isNotBlank()) {
                HorizontalDivider(
                    thickness = 0.3.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.3f)
                )
            }
            Label(
                label = label.first,
                amount = label.second,
                base = label.third
            )
        }
    }
}


@Composable
private fun Label(
    label: String,
    amount: String,
    base: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.weight(1f))

        val (fontWeight, color) = if (amount.isBlank() || amount.startsWith("0 ") || amount.startsWith(
                "0.0 "
            )
        ) {
            Pair(FontWeight.Normal, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        } else {
            Pair(FontWeight.Medium, MaterialTheme.colorScheme.onSurface)
        }
        Text(
            text = amount,
            maxLines = 1,
            fontWeight = fontWeight,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            color = color
        )
        Text(
            text = "/$base",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

private fun MacrosSummary.macroGoals(context: Context): List<Triple<String, String, String>> {
    return listOf(
        Triple(
            context.getString(R.string.protein),
            "${actual.protein.roundDecimals()}",
            "${goal.protein} g"
        ),
        Triple(
            context.getString(R.string.carbs),
            "${actual.carbohydrates.roundDecimals()}",
            "${goal.carbohydrates} g"
        ),
        Triple(
            context.getString(R.string.fats),
            "${actual.fats.roundDecimals()}",
            "${goal.carbohydrates} g"
        ),
    )
}


private fun Minerals.rda(
    context: Context,
    mineralsRda: RdaCalculator.MineralsRDA
): List<Triple<String, String, String>> {
    val minerals = this
    return listOf(
        Triple(
            context.getString(R.string.calcium),
            "${minerals.calcium.roundToInt()}",
            "${mineralsRda.calcium} mg"
        ),
        Triple(
            context.getString(R.string.iron),
            "${minerals.iron.roundToInt()}",
            "${mineralsRda.iron} mg"
        ),
        Triple(
            context.getString(R.string.magnesium),
            "${minerals.magnesium.roundToInt()}",
            "${mineralsRda.magnesium} mg"
        ),
        Triple(
            context.getString(R.string.potassium),
            "${minerals.potassium.roundToInt()}",
            "${mineralsRda.potassium} mg"
        ),
        Triple(
            context.getString(R.string.sodium),
            "${minerals.sodium.roundToInt()}",
            "${mineralsRda.sodium} mg"
        ),
    )
}


private fun Vitamins.rda(
    context: Context,
    vitaminsRda: RdaCalculator.VitaminsRDA
): List<Triple<String, String, String>> {
    val vitamins = this
    return listOf(
        Triple(
            context.getString(R.string.vitaminA),
            "${vitamins.vitaminA.roundToInt()}",
            "${vitaminsRda.vitaminA} μg"
        ),
        Triple(
            context.getString(R.string.vitaminB1),
            "${vitamins.vitaminB1.roundToInt()}",
            "${vitaminsRda.vitaminB1} mg"
        ),
        Triple(
            context.getString(R.string.vitaminB2),
            "${vitamins.vitaminB2.roundToInt()}",
            "${vitaminsRda.vitaminB2} mg"
        ),
        Triple(
            context.getString(R.string.vitaminB3),
            "${vitamins.vitaminB3.roundToInt()}",
            "${vitaminsRda.vitaminB3} mg"
        ),
        Triple(
            context.getString(R.string.vitaminB4),
            "${vitamins.vitaminB4.roundToInt()}",
            "${vitaminsRda.vitaminB4} mg"
        ),
        Triple(
            context.getString(R.string.vitaminB5),
            "${vitamins.vitaminB5.roundToInt()}",
            "${vitaminsRda.vitaminB5} mg"
        ),
        Triple(
            context.getString(R.string.vitaminB6),
            "${vitamins.vitaminB6.roundToInt()}",
            "${vitaminsRda.vitaminB6} mg"
        ),
        Triple(
            context.getString(R.string.vitaminB9),
            "${vitamins.vitaminB9.roundToInt()}",
            "${vitaminsRda.vitaminB9} μg"
        ),
        Triple(
            context.getString(R.string.vitaminB12),
            "${vitamins.vitaminB12.roundToInt()}",
            "${vitaminsRda.vitaminB12} μg"
        ),
        Triple(
            context.getString(R.string.vitaminC),
            "${vitamins.vitaminC.roundToInt()}",
            "${vitaminsRda.vitaminC} mg"
        ),
        Triple(
            context.getString(R.string.vitaminD),
            "${vitamins.vitaminD.roundToInt()}",
            "${vitaminsRda.vitaminD} μg"
        ),
        Triple(
            context.getString(R.string.vitaminE),
            "${vitamins.vitaminE.roundToInt()}",
            "${vitaminsRda.vitaminE} mg"
        ),
        Triple(
            context.getString(R.string.vitaminK),
            "${vitamins.vitaminK.roundToInt()}",
            "${vitaminsRda.vitaminK} μg"
        ),
    )
}


fun mineralsScoreCalculator(
    summary: Minerals,
    mineralsRda: RdaCalculator.MineralsRDA,
): Int {
    fun scoreValue(value: Double): Int {
        return when {
            value >= 90.0 -> 100
            else -> {
                val raw = (value / 90.0).coerceIn(0.0, 1.0)
                (raw * 100.0).roundToInt()
            }
        }
    }

    val calciumScore = scoreValue(summary.calcium / mineralsRda.calcium * 100)
    val ironScore = scoreValue(summary.iron / mineralsRda.iron * 100)
    val magnesiumScore = scoreValue(summary.magnesium / mineralsRda.magnesium * 100)
    val potassiumScore = scoreValue(summary.potassium / mineralsRda.potassium * 100)
    val sodiumScore = scoreValue(summary.sodium / mineralsRda.sodium * 100)
    val totalScore = (calciumScore + ironScore + magnesiumScore + potassiumScore + sodiumScore) / 5
    return totalScore
}

fun vitaminsScoreCalculator(
    summary: Vitamins,
    vitaminsRda: RdaCalculator.VitaminsRDA,
): Int {
    fun scoreValue(value: Double): Int {
        return when {
            value >= 90.0 -> 100
            else -> {
                val raw = (value / 90.0).coerceIn(0.0, 1.0)
                (raw * 100.0).roundToInt()
            }
        }
    }

    val vitaminAScore = scoreValue(summary.vitaminA / vitaminsRda.vitaminA * 100)
    val vitaminB1Score = scoreValue(summary.vitaminB1 / vitaminsRda.vitaminB1 * 100)
    val vitaminB2Score = scoreValue(summary.vitaminB2 / vitaminsRda.vitaminB2 * 100)
    val vitaminB3Score = scoreValue(summary.vitaminB3 / vitaminsRda.vitaminB3 * 100)
    val vitaminB4Score = scoreValue(summary.vitaminB4 / vitaminsRda.vitaminB4 * 100)
    val vitaminB5Score = scoreValue(summary.vitaminB5 / vitaminsRda.vitaminB5 * 100)
    val vitaminB6Score = scoreValue(summary.vitaminB6 / vitaminsRda.vitaminB6 * 100)
    val vitaminB9Score = scoreValue(summary.vitaminB9 / vitaminsRda.vitaminB9 * 100)
    val vitaminB12Score = scoreValue(summary.vitaminB12 / vitaminsRda.vitaminB12 * 100)
    val vitaminCScore = scoreValue(summary.vitaminC / vitaminsRda.vitaminC * 100)
    val vitaminDScore = scoreValue(summary.vitaminD / vitaminsRda.vitaminD * 100)
    val vitaminEScore = scoreValue(summary.vitaminE / vitaminsRda.vitaminE * 100)
    val vitaminKScore = scoreValue(summary.vitaminK / vitaminsRda.vitaminK * 100)
    val totalScore = (vitaminAScore + vitaminCScore + vitaminDScore + vitaminEScore + vitaminKScore
            + vitaminB1Score + vitaminB2Score + vitaminB3Score + vitaminB4Score + vitaminB5Score
            + vitaminB6Score + vitaminB9Score + vitaminB12Score)
    return totalScore / 13
}

