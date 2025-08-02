package com.xcvi.micros.ui.screens.details

import android.content.Context
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.usecases.DetailsUseCases
import com.xcvi.micros.ui.BaseViewModel

data class DetailsState(
    val portion: Portion? = null
)

class DetailsViewModel(
    applicationContext: Context,
    private val useCases: DetailsUseCases,
) : BaseViewModel<DetailsState>(DetailsState())