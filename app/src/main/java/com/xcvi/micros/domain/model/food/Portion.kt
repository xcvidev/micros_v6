package com.xcvi.micros.domain.model.food

data class Portion(
    val barcode: String,
    val name: String,
    val amount: Int,

    val date: Int,
    val meal: Int,

    val isFavorite: Boolean,

    val nutrients: Nutrients,
    val minerals: Minerals,
    val vitamins: Vitamins,
    val aminoAcids: AminoAcids
) 