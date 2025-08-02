package com.xcvi.micros.data.source.remote.dto
import kotlinx.serialization.Serializable

@Serializable
data class EnhancedDTO(
    val minerals: MineralsDTO,
    val vitamins: VitaminsDTO,
    val aminoAcids: AminoAcidsDTO
){
    fun isNotEmpty(): Boolean {
        return minerals.isNotEmpty() || vitamins.isNotEmpty() || aminoAcids.isNotEmpty()
    }
}

@Serializable
data class VitaminsDTO(
    val vitaminA: Double = 0.0, // micro g
    val vitaminB1: Double = 0.0, // mg
    val vitaminB2: Double = 0.0, // mg
    val vitaminB3: Double = 0.0, // mg
    val vitaminB4: Double = 0.0, // mg
    val vitaminB5: Double = 0.0, // mg
    val vitaminB6: Double = 0.0, // mg
    val vitaminB9: Double = 0.0, // micro g
    val vitaminB12: Double = 0.0, // micro g
    val vitaminC: Double = 0.0, // mg
    val vitaminD: Double = 0.0, // micro g
    val vitaminE: Double = 0.0, // mg
    val vitaminK: Double = 0.0  // micro g
){
    fun isNotEmpty(): Boolean {
        return vitaminA > 0 || vitaminC > 0 || vitaminD > 0 || vitaminE > 0 || vitaminK > 0
                || vitaminB1 > 0 || vitaminB2 > 0 || vitaminB3 > 0 || vitaminB4 > 0
                || vitaminB5 > 0 || vitaminB6 > 0 || vitaminB9 > 0 || vitaminB12 > 0
    }
}

@Serializable
data class MineralsDTO(
    var potassium: Double = 0.0, // mg
    var calcium: Double = 0.0, // mg
    var magnesium: Double = 0.0, // mg
    var iron: Double = 0.0, // mg
    var sodium: Double = 0.0, // mg
    var zinc: Double = 0.0, // mg
    var fluoride: Double = 0.0, // mg
    var iodine: Double = 0.0, // mg
    var phosphorus: Double = 0.0, // mg
    var manganese: Double = 0.0, // mg
    var selenium: Double = 0.0, // mg
){
    fun isNotEmpty(): Boolean {
        return potassium > 0 || calcium > 0 || magnesium > 0 || iron > 0 || sodium > 0
    }
}

@Serializable
data class AminoAcidsDTO(
    val histidine: Double = 0.0,
    val isoleucine: Double = 0.0,
    val leucine: Double = 0.0,
    val lysine: Double = 0.0,
    val methionine: Double = 0.0,
    val phenylalanine: Double = 0.0,
    val threonine: Double = 0.0,
    val tryptophan: Double = 0.0,
    val valine: Double = 0.0
){
    fun isNotEmpty(): Boolean {
        return histidine > 0 || isoleucine > 0 || leucine > 0
                || lysine > 0 || methionine > 0 || phenylalanine > 0
                || threonine > 0 || tryptophan > 0 || valine > 0
    }

}