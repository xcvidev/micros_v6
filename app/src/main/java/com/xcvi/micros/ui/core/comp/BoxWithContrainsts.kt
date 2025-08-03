package com.xcvi.micros.ui.core.comp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun BoxWithConstraintsExample (modifier: Modifier = Modifier) {

    /*
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val totalHeight = maxHeight
        val weights = listOf(2f, 1f, 1f, 3f)
        val totalWeight = weights.sum()

        val heights = weights.map { weight -> totalHeight * (weight / totalWeight) }

        Column(Modifier.fillMaxSize()) {
            Elem1(modifier = Modifier.height(heights[0]))
            Elem2(modifier = Modifier.height(heights[1]))
            Elem3(modifier = Modifier.height(heights[2]))
            Elem4(modifier = Modifier.height(heights[3]))
        }
    }

     */
}