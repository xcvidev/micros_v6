package com.xcvi.micros.ui.screens.search

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.ui.core.comp.AnimatedDots

@Composable
fun SmartSearchButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    searching: Boolean,
    onClick: () -> Unit
) {

    val transition = updateTransition(targetState = searching, label = "")

    val iconAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "icon_alpha"
    ) { isClicked -> if (isClicked) 0f else 1f }

    val buttonColor by transition.animateColor(
        label = ""
    ) { isClicked ->
        if (isClicked) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.primary
    }

    OutlinedButton(
        enabled = enabled,
        modifier = modifier,
        onClick = {
            onClick()
        },
        border = BorderStroke(2.dp, buttonColor),
        //colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        if (searching) {
            AnimatedDots(
                style = MaterialTheme.typography.labelLarge,
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.smart_search),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    modifier = Modifier.alpha(iconAlpha).size(16.dp),
                    painter = painterResource(R.drawable.ic_ai_filled),
                    contentDescription = null
                )
            }
        }
    }
}