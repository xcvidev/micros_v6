package com.xcvi.micros.ui.screens.stats

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.utils.FilterType
import com.xcvi.micros.domain.model.weight.WeightUnit
import com.xcvi.micros.domain.usecases.StatsUseCases
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class StatsViewModel(
    private val useCases: StatsUseCases,
) : BaseViewModel<StatsState>(StatsState()) {

    init {
        observeData(null)
    }

    fun onEvent(event: StatsEvent) {
        when (event) {
            is StatsEvent.ChangeYear -> observeData(event.year)
            is StatsEvent.ChangeFilter -> updateData {
                copy(
                    filter = event.filter
                )
            }
        }
    }

    private var observer: Job? = null
    private fun observeData(year: Int?) {
        observer?.cancel()
        observer = viewModelScope.launch {
            val data = useCases.getData()
            val years = data.first

            val unit = data.third.firstOrNull()?.unit ?: WeightUnit.kg

            val weightsByDay = useCases.filterWeights(year, FilterType.DAY, data.third, unit)
            val weightsByWeek = useCases.filterWeights(year, FilterType.WEEK, data.third, unit)
            val weightsByMonth = useCases.filterWeights(year, FilterType.MONTH, data.third, unit)
            val foodsByDay = useCases.filterFoods(year, FilterType.DAY, data.second)
            val foodsByWeek = useCases.filterFoods(year, FilterType.WEEK, data.second)
            val foodsByMonth = useCases.filterFoods(year, FilterType.MONTH, data.second)

            val defaultFilter = if(foodsByWeek.size > 1 && weightsByWeek.size > 1){
                FilterType.WEEK
            } else {
                FilterType.DAY
            }
            updateData {
                copy(
                    filter = defaultFilter,
                    years = years,
                    weightsByDay = weightsByDay,
                    weightsByWeek = weightsByWeek,
                    weightsByMonth = weightsByMonth,
                    macrosByDay = foodsByDay,
                    macrosByWeek = foodsByWeek,
                    macrosByMonth = foodsByMonth,
                    caloriesByDay = foodsByDay.map { it.actual.calories.takeIf { c-> c > 0 }?.toDouble() },
                    calorieByWeek = foodsByWeek.map { it.actual.calories.takeIf { c-> c > 0 }?.toDouble() },
                    caloriesByMonth = foodsByMonth.map { it.actual.calories.takeIf { c-> c > 0 }?.toDouble() },
                )
            }
        }
    }
}