package com.xcvi.micros.domain.model.food

data class Food(
    val barcode: String,
    val name: String,

    val isAI: Boolean,
    val isFavorite: Boolean,
    val isRecent: Boolean,

    val nutrients: Nutrients,
    val minerals: Minerals,
    val vitamins: Vitamins,
    val aminoAcids: AminoAcids
){
    companion object{
        fun empty() = Food(
            barcode = "",
            name = "",
            isAI = false,
            isFavorite = false,
            isRecent = false,
            nutrients = Nutrients.empty(),
            minerals = Minerals.empty(),
            vitamins = Vitamins.empty(),
            aminoAcids = AminoAcids.empty()
        )
    }
}

