package com.xcvi.micros.domain.model.food

data class Food(
    val barcode: String,
    val isAI: Boolean,

    val name: String,
    val isFavorite: Boolean,
    val isRecent: Boolean,

    val nutrients: Nutrients,
    val minerals: Minerals,
    val vitamins: Vitamins,
    val aminoAcids: AminoAcids
)

