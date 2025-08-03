package com.xcvi.micros.ui.core.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


/**
 * headlineSmall for headline text
 * titleSmall for subhead text with onSurfaceVariant
 * bodyMedium for body text with onSurfaceVariant
 */
@Composable
fun M3Card(
    modifier: Modifier = Modifier,
    headline: @Composable () -> Unit,
    subhead: (@Composable () -> Unit)? = null,
    body: (@Composable () -> Unit)? = null,
    media: (@Composable () -> Unit)? = null,
    mediaAlignment: Alignment? = Alignment.TopStart,
    action: (@Composable () -> Unit)? = null,
    actionAlignment: Alignment = Alignment.BottomEnd,
    containerColor: Color? = null
) {
    Card(
        colors =  if(containerColor == null){
            CardDefaults.cardColors()
        } else{
            CardDefaults.cardColors(
                containerColor = containerColor,
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Box(Modifier.padding(bottom = 4.dp)) {
                ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                    headline()
                }
            }
            if (subhead != null) {
                Box(Modifier.padding(vertical = 4.dp)) {
                    ProvideTextStyle(MaterialTheme.typography.titleSmall) {
                        subhead()
                    }
                }
            }
            if(body!=null){
                Box(Modifier.padding(vertical = 4.dp)) {
                    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                        body()
                    }
                }
            }
            if(media !=null){
                Box(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = mediaAlignment ?: Alignment.Center
                ) {
                    media()
                }
            }

            if (action != null){
                Box(Modifier.fillMaxWidth(), contentAlignment = actionAlignment) {
                    action()
                }
            }

        }
    }
}



@Composable
fun M3Card(
    modifier: Modifier = Modifier,
    headline: String = "Display small",
    subhead: String = "Subhead",
    supportingText: String = "Explain more about the topic in the display and subhead through supporting text.",
    onActionClick: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()              // Default  guideline padding
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)              // Default  guideline padding

            )

            Text(
                text = subhead,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)              // Default  guideline padding

            )


            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)              // Default  guideline padding
            )


            // Placeholder for media
            Spacer(modifier = Modifier.height(20.dp))


            // Action Button
            Button(
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Action")
            }
        }
    }
}


@Composable
fun M3CardWithMedia(
    modifier: Modifier = Modifier,
    headline: String = "Display small",
    subhead: String = "Subhead",
    supportingText: String = "Explain more about the topic in the display and subhead through supporting text.",
    onActionClick: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()              // Default  guideline padding
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)              // Default  guideline padding

            )

            Text(
                text = subhead,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)              // Default  guideline padding

            )


            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)              // Default  guideline padding
            )


            // Placeholder for media
            Box(
                modifier = Modifier
                    .padding(vertical = 20.dp)              // Default  guideline padding
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )


            // Action Button
            Button(
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Action")
            }
        }
    }
}


@Composable
fun StreamingTextCard(
    isStreaming: Boolean,
    title: String,
    subtitle: String,
    body: String,
    modifier: Modifier = Modifier,
    charDelayMillis: Long = 30L,
    onClick: () -> Unit,
    onFinished: (() -> Unit)? = null,
    action: @Composable () -> Unit = {},
) {
    var visibleHeadline by remember(title) { mutableStateOf("") }
    var visibleSubhead by remember(subtitle) { mutableStateOf("") }
    var visibleBody by remember(body) { mutableStateOf("") }

    LaunchedEffect(Unit) {
        visibleHeadline = ""
        for (i in title.indices) {
            visibleHeadline += title[i]
            if (isStreaming) {
                delay(charDelayMillis)
            }
        }

        visibleSubhead = ""
        for (i in subtitle.indices) {
            visibleSubhead += subtitle[i]
            if (isStreaming) {
                delay(charDelayMillis)
            }
        }

        visibleBody = ""
        for (i in body.indices) {
            visibleBody += body[i]
            if (isStreaming) {
                delay(charDelayMillis)
            }
        }

        onFinished?.invoke()
    }


    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(top = 12.dp, start = 18.dp, end = 18.dp, bottom = 24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = visibleHeadline,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = visibleSubhead,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = visibleBody,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Box(modifier = Modifier.align(Alignment.End)) { action() }
        }
    }
}



