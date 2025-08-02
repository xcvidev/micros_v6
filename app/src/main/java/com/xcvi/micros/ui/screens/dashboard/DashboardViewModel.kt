package com.xcvi.micros.ui.screens.dashboard

import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.usecases.DashboardUseCase
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.ui.BaseViewModel

data class DashboardState(
    val currentDate: Int = getToday()
)
class DashboardViewModel(
    private val dashboardUseCase: DashboardUseCase,
): BaseViewModel<DashboardState>(DashboardState()) {

}