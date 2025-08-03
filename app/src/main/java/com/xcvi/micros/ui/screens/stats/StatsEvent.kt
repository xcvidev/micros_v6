package com.xcvi.micros.ui.screens.stats

import com.xcvi.micros.domain.model.utils.FilterType


sealed interface StatsEvent {
    data class ChangeYear(val year: Int) : StatsEvent
    data class ChangeFilter(val filter: FilterType) : StatsEvent
}