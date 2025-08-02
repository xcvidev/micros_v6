package com.xcvi.micros.domain.model.message

import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Vitamins

data class FoodItem(
    val id: String,
    val name: String,
    val amount: Int,
    val nutrients: Nutrients,
    val minerals: Minerals,
    val vitamins: Vitamins = Vitamins.empty(),
    val aminoAcids: AminoAcids = AminoAcids.empty()
)