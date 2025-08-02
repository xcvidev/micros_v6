package com.xcvi.micros.ui.screens.meal

import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.usecases.MealUseCases
import com.xcvi.micros.ui.BaseViewModel

data class MealState(
    val deletePortion: Portion? = null,
)


class MealViewModel(
    private val useCases: MealUseCases
) : BaseViewModel<MealState>(MealState())