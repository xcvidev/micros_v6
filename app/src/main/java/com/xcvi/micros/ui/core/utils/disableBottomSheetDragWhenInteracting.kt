package com.xcvi.micros.ui.core.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll

fun Modifier.disableBottomSheetDragWhenInteracting(): Modifier {
    val dragInterceptor = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // Consume vertical drag so it doesn't propagate to sheet
            return if (available.y != 0f) Offset(x = 0f, y = available.y) else Offset.Zero
        }
    }
    return this.nestedScroll(dragInterceptor)
}
