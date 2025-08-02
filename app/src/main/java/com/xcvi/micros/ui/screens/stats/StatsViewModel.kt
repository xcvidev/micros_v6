package com.xcvi.micros.ui.screens.stats

import com.xcvi.micros.domain.model.utils.FilterType
import com.xcvi.micros.domain.usecases.StatsUseCases
import com.xcvi.micros.ui.BaseViewModel

data class StatsState(
    val filter: FilterType = FilterType.WEEK
)
class StatsViewModel(
    private val useCases: StatsUseCases,
) : BaseViewModel<StatsState>(StatsState())