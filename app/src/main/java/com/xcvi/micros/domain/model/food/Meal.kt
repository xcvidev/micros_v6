package com.xcvi.micros.domain.model.food

data class Meal(
    val portions: List<Portion>,
    val name: String,
    val isPinned: Boolean,
    val isVisible: Boolean,
    val date: Int,
    val number: Int,
    val nutrients: Nutrients,
    val minerals: Minerals,
    val vitamins: Vitamins,
    val aminoAcids: AminoAcids
)