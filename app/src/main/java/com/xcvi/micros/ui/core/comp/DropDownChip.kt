package com.xcvi.micros.ui.core.comp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun <T> DropDownChip(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    toString: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }
    Box {

        Box(modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }) {
            Row(
                modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = toString(selectedOption), color = contentColor)
                if (imageVector != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = contentColor
                    )
                }
            }
        }
        DropdownMenu(
            modifier = Modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(
                topEnd = 0.dp,
                topStart = 0.dp,
                bottomEnd = 12.dp,
                bottomStart = 12.dp
            )
            //containerColor = Color.Transparent
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = toString(option),
                            textAlign = TextAlign.Center,
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }

    }
}