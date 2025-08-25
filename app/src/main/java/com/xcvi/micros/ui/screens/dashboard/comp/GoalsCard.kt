package com.xcvi.micros.ui.screens.dashboard.comp


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Macros
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.macroScoreAlgorithm
import com.xcvi.micros.domain.model.food.scoreCalories
import com.xcvi.micros.domain.model.food.scoreCarbs
import com.xcvi.micros.domain.model.food.scoreFats
import com.xcvi.micros.domain.model.food.scoreProtein
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.core.comp.M3Card

@Composable
fun GoalsCard(
    summary: MacrosSummary,
    onClick: () -> Unit = {},
) {
    val headline = stringResource(R.string.goals_title)
    Box(
        modifier = Modifier.padding(4.dp)
    ) {

        if (!summary.hasGoals()) {
            M3Card(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick() },
                headline = {

                    Text(text = headline)
                },
                body = {
                    val info1 = stringResource(R.string.goals_info_1)
                    val info2 = stringResource(R.string.goals_info_2a)
                    val info3 = stringResource(R.string.goals_info_2b)
                    val ai = stringResource(R.string.ai)
                    val text = buildAnnotatedString {
                        appendLine(info1)
                        append(info2)
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(" $ai ")
                        }
                        append(info3)
                    }
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                    )
                }
            )
        } else {
            M3Card(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick() },
                headline = {
                    Text(text = stringResource(R.string.goals_title))
                },
                subhead = {
                    val progress = macrosScoreCalculator(summary.actual, summary.goal)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.progress),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        ScoreBar(progress)
                    }
                },
                body = {
                    val errorDot = MaterialTheme.colorScheme.error
                    val underScoreDot = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    val completedDot = MaterialTheme.colorScheme.primary

                    fun dotColor(current: Double, goal: Double, getScore: (Double) -> Double): Color {
                        val percent = current / goal * 100
                        val score = getScore(percent)
                        return if (score == 1.0) {
                            completedDot
                        } else if(score < 1.0) {
                            underScoreDot
                        } else {
                            errorDot
                        }
                    }
                    Column(Modifier.padding(vertical = 4.dp)) {
                        GoalText(
                            name = stringResource(R.string.protein),
                            goal = summary.goal.protein.roundToInt(),
                            current = summary.actual.protein,
                            dotColor = dotColor(summary.actual.protein, summary.goal.protein,::scoreProtein)
                        )
                        GoalText(
                            name = stringResource(R.string.carbs),
                            goal = summary.goal.carbohydrates.roundToInt(),
                            current = summary.actual.carbohydrates,
                            dotColor = dotColor(summary.actual.carbohydrates, summary.goal.carbohydrates,::scoreCarbs)
                        )
                        GoalText(
                            name = stringResource(R.string.fats),
                            goal = summary.goal.fats.roundToInt(),
                            current = summary.actual.fats,
                            dotColor = dotColor(summary.actual.fats, summary.goal.fats, ::scoreFats)
                        )
                        GoalText(
                            name = stringResource(R.string.calories),
                            goal = summary.goal.calories,
                            current = summary.actual.calories * 1.0,
                            dotColor = dotColor(summary.actual.calories.toDouble(), summary.goal.calories.toDouble(), ::scoreCalories)
                        )
                    }
                }
            )
        }

    }
}


fun macrosScoreCalculator(actual: Macros, goal: Macros): Int {
    val proteinProgress = actual.protein / goal.protein * 100
    val carbProgress = actual.carbohydrates / goal.carbohydrates * 100
    val fatProgress = actual.fats / goal.fats * 100

    return macroScoreAlgorithm(protein = proteinProgress, carbs = carbProgress, fats = fatProgress)
}


@Composable
fun ScoreBar(
    score: Int,
    modifier: Modifier = Modifier,
    barHeight: Dp = 8.dp,
) {
    val weight = score / 100f
    val barColor = when(weight){
        in 0f..0.5f -> MaterialTheme.colorScheme.onSurface.copy(0.6f)
        else -> MaterialTheme.colorScheme.primary.copy(weight)
    }
    Column(
        modifier = modifier,
    ) {
        // Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .fillMaxWidth()
                .height(barHeight)
                .background(MaterialTheme.colorScheme.onSurface.copy(0.2f))
        ) {

            Box(
                modifier = Modifier
                    .height(barHeight + 1.dp)
                    .fillMaxWidth(weight)
                    .background(barColor)
            )
        }
    }
}


@Composable
fun GoalText(
    name: String,
    goal: Int,
    current: Double,
    modifier: Modifier = Modifier,
    dotColor: Color,
) {


    Column(
        modifier = modifier,
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(6.dp)
                    .background(dotColor)
            )

            val text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${current.roundToInt()}")
                }
                append(" / $goal")
            }
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
            )
        }

    }
}
