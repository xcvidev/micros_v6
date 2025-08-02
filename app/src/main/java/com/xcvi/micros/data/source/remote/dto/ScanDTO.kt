package com.xcvi.micros.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanDTO(
    val product: ScanProductDTO? = null,
    val code: String = "",
    val status: String = "",
)

@Serializable
data class ScanProductDTO(
    @SerialName("code")
    val barcode: String = "",
    @SerialName("product_name")
    val name: String = "",
    val brands: String = "",
    val nutriments: NutrimentsDTO = NutrimentsDTO(),
    val nutriments_estimated: NutrimentsEstimatedDTO = NutrimentsEstimatedDTO(),
)


@Serializable
data class NutrimentsDTO(
    val carbohydrates_100g: Double = -1.0,
    @SerialName("energy-kcal_100g")
    val kcal: Double = -1.0,
    val fat_100g: Double = -1.0,
    val fiber_100g: Double = -1.0,
    val proteins_100g: Double = -1.0,
    val salt_100g: Double = -1.0,
    @SerialName("saturated-fat_100g")
    val saturated_fat_100g: Double = -1.0,
    val sugars_100g: Double = -1.0,

    val potassium_100g: Double = -1.0,
    val calcium_100g: Double = -1.0,
    val magnesium_100g: Double = -1.0,
    val iron_100g: Double = -1.0,
    val sodium_100g: Double = -1.0,
    val zinc_100g: Double = -1.0,
    val iodine_100g: Double = -1.0,
    val manganese_100g: Double = -1.0,
    val phosphorus_100g: Double = -1.0,
    val selenium_100g: Double = -1.0,
    @SerialName("fluoride_100g")
    val fluoride: Double= -1.0,

    @SerialName("vitamin-a_100g")
    val vitaminA: Double = -1.0,
    @SerialName("vitamin-b12_100g")
    val vitaminB12: Double = -1.0,
    @SerialName("vitamin-b1_100g")
    val vitaminB1: Double = -1.0,
    @SerialName("vitamin-b2_100g")
    val vitaminB2: Double = -1.0,
    @SerialName("vitamin-b4_100g")
    val vitaminB4: Double = -1.0,
    @SerialName("vitamin-pp_100g")
    val vitaminB3: Double = -1.0,
    @SerialName("pantothenic-acid_100g")
    val vitaminB5: Double= -1.0,
    @SerialName("vitamin-b6_100g")
    val vitaminB6: Double = -1.0,
    @SerialName("vitamin-b9_100g")
    val vitaminB9: Double = -1.0,
    @SerialName("vitamin-c_100g")
    val vitaminC: Double = -1.0,
    @SerialName("vitamin-d2_100g")
    val vitaminD2: Double = -1.0,
    @SerialName("vitamin-d3_100g")
    val vitaminD3: Double = -1.0,
    @SerialName("vitamin-d_100g")
    val vitaminD: Double = -1.0,
    @SerialName("vitamin-e_100g")
    val vitaminE: Double = -1.0,
    @SerialName("vitamin-k_100g")
    val vitaminK: Double = -1.0
)


@Serializable
data class NutrimentsEstimatedDTO(
    val carbohydrates_100g: Double = 0.0,
    @SerialName("energy-kcal_100g")
    val kcal: Double = 0.0,
    val fat_100g: Double = 0.0,
    val fiber_100g: Double = 0.0,
    val proteins_100g: Double = 0.0,
    val salt_100g: Double = 0.0,
    @SerialName("saturated-fat_100g")
    val saturated_fat_100g: Double = 0.0,
    val sugars_100g: Double = 0.0,

    val potassium_100g: Double = 0.0,
    val calcium_100g: Double = 0.0,
    val magnesium_100g: Double = 0.0,
    val iron_100g: Double = 0.0,
    val sodium_100g: Double = 0.0,
    val zinc_100g: Double = 0.0,
    val iodine_100g: Double = 0.0,
    val manganese_100g: Double = 0.0,
    val phosphorus_100g: Double = 0.0,
    val selenium_100g: Double = 0.0,
    @SerialName("fluoride_100g")
    val fluoride: Double= 0.0,

    @SerialName("vitamin-a_100g")
    val vitaminA: Double = 0.0,
    @SerialName("vitamin-b12_100g")
    val vitaminB12: Double = 0.0,
    @SerialName("vitamin-b1_100g")
    val vitaminB1: Double = 0.0,
    @SerialName("vitamin-b2_100g")
    val vitaminB2: Double = 0.0,
    @SerialName("vitamin-b4_100g")
    val vitaminB4: Double = 0.0,
    @SerialName("vitamin-pp_100g")
    val vitaminB3: Double = 0.0,
    @SerialName("pantothenic-acid_100g")
    val vitaminB5: Double= 0.0,
    @SerialName("vitamin-b6_100g")
    val vitaminB6: Double = 0.0,
    @SerialName("vitamin-b9_100g")
    val vitaminB9: Double = 0.0,
    @SerialName("vitamin-c_100g")
    val vitaminC: Double = 0.0,
    @SerialName("vitamin-d2_100g")
    val vitaminD2: Double = 0.0,
    @SerialName("vitamin-d3_100g")
    val vitaminD3: Double = 0.0,
    @SerialName("vitamin-d_100g")
    val vitaminD: Double = 0.0,
    @SerialName("vitamin-e_100g")
    val vitaminE: Double = 0.0,
    @SerialName("vitamin-k_100g")
    val vitaminK: Double = 0.0
)
/*
@kotlinx.serialization.Serializable
data class NutrimentsDTO(
    val alcohol_100g: Double = -1.0,
    val calcium_100g: Double = -1.0,
    val carbohydrates_100g: Double = -1.0,
    val cholesterol_100g: Double = -1.0,
    val copper_100g: Double = -1.0,
    @SerialName("energy-kcal_100g")
    val kcal: Double = -1.0,
    val fat_100g: Double = -1.0,
    val fiber_100g: Double = -1.0,
    val fructose_100g: Double = -1.0,
    val galactose_100g: Double = -1.0,
    val glucose_100g: Double = -1.0,
    val iodine_100g: Double = -1.0,
    val iron_100g: Double = -1.0,
    val lactose_100g: Double = -1.0,
    val magnesium_100g: Double = -1.0,
    val maltose_100g: Double = -1.0,
    val manganese_100g: Double = -1.0,
    val phosphorus_100g: Double = -1.0,
    val phylloquinone_100g: Double = -1.0,
    val polyols_100g: Double = -1.0,
    val potassium_100g: Double = -1.0,
    val proteins_100g: Double = -1.0,
    val salt_100g: Double = -1.0,
    @SerialName("saturated-fat_100g")
    val saturated_fat_100g: Double = -1.0,
    @SerialName("monounsaturated-fat_100g")
    val monounsaturated_fat_100g: Double= -1.0,
    @SerialName("polyunsaturated-fat_100g")
    val polyunsaturated_fat_100g: Double= -1.0,
    @SerialName("trans-fat_100g")
    val trans_fat_100g: Double= -1.0,
    val selenium_100g: Double = -1.0,
    val sodium_100g: Double = -1.0,
    val starch_100g: Double = -1.0,
    val sucrose_100g: Double = -1.0,
    val sugars_100g: Double = -1.0,

    @SerialName("vitamin-a_100g")
    val vitaminA: Double = -1.0,
    @SerialName("vitamin-b12_100g")
    val vitaminB12: Double = -1.0,
    @SerialName("vitamin-b1_100g")
    val vitaminB1: Double = -1.0,
    @SerialName("vitamin-b2_100g")
    val vitaminB2: Double = -1.0,
    @SerialName("vitamin-b4_100g")
    val vitaminB4: Double = -1.0,
    @SerialName("vitamin-pp_100g")
    val vitaminB3: Double = -1.0,
    @SerialName("pantothenic-acid_100g")
    val vitaminB5: Double= 0.0,
    @SerialName("vitamin-b6_100g")
    val vitaminB6: Double = -1.0,
    @SerialName("vitamin-b9_100g")
    val vitaminB9: Double = -1.0,
    @SerialName("vitamin-c_100g")
    val vitaminC: Double = -1.0,
    @SerialName("vitamin-d2_100g")
    val vitaminD2: Double = -1.0,
    @SerialName("vitamin-d3_100g")
    val vitaminD3: Double = -1.0,
    @SerialName("vitamin-d_100g")
    val vitaminD: Double = -1.0,
    @SerialName("vitamin-e_100g")
    val vitaminE: Double = -1.0,
    @SerialName("vitamin-k_100g")
    val vitaminK: Double = -1.0,

    val water_100g: Double = -1.0,
    val zinc_100g: Double = -1.0,
    @SerialName("beta-carotene_100g")
    val betaCarotene: Double= -1.0,
    @SerialName("fluoride_100g")
    val fluoride: Double= -1.0,
    @SerialName("alpha-carotene_100g")
    val alphaCarotene: Double= -1.0

)

@Serializable
data class NutrimentsEstimatedDTO(
    val alcohol_100g: Double = 0.0,
    val calcium_100g: Double= 0.0,
    val carbohydrates_100g: Double= 0.0,
    val cholesterol_100g: Double= 0.0,
    val copper_100g: Double = 0.0,
    @SerialName("energy-kcal_100g")
    val kcal: Double= 0.0,
    val fat_100g: Double= 0.0,
    val fiber_100g: Double= 0.0,
    val fructose_100g: Double= 0.0,
    val galactose_100g: Double= 0.0,
    val glucose_100g: Double= 0.0,
    val iodine_100g: Double= 0.0,
    val iron_100g: Double= 0.0,
    val lactose_100g: Double= 0.0,
    val magnesium_100g: Double= 0.0,
    @SerialName("monounsaturated-fat_100g")
    val monounsaturated_fat_100g: Double= 0.0,
    @SerialName("polyunsaturated-fat_100g")
    val polyunsaturated_fat_100g: Double= 0.0,
    @SerialName("trans-fat_100g")
    val trans_fat_100g: Double= 0.0,
    val maltose_100g: Double= 0.0,
    val manganese_100g: Double= 0.0,

    val phosphorus_100g: Double= 0.0,
    val phylloquinone_100g: Double= 0.0,
    val polyols_100g: Double= 0.0,
    val potassium_100g: Double= 0.0,
    val proteins_100g: Double= 0.0,
    val salt_100g: Double= 0.0,
    @SerialName("saturated-fat_100g")
    val saturated_fat_100g: Double= 0.0,
    val selenium_100g: Double= 0.0,
    val sodium_100g: Double= 0.0,
    val starch_100g: Double= 0.0,
    val sucrose_100g: Double= 0.0,
    val sugars_100g: Double= 0.0,


    @SerialName("vitamin-a_100g")
    val vitaminA: Double = 0.0,
    @SerialName("vitamin-b12_100g")
    val vitaminB12: Double = 0.0,
    @SerialName("vitamin-b1_100g")
    val vitaminB1: Double = 0.0,
    @SerialName("vitamin-b2_100g")
    val vitaminB2: Double = 0.0,
    @SerialName("vitamin-b4_100g")
    val vitaminB4: Double = 0.0,
    @SerialName("vitamin-pp_100g")
    val vitaminB3: Double = 0.0,
    @SerialName("pantothenic-acid_100g")
    val vitaminB5: Double= 0.0,
    @SerialName("vitamin-b6_100g")
    val vitaminB6: Double = 0.0,
    @SerialName("vitamin-b9_100g")
    val vitaminB9: Double = 0.0,
    @SerialName("vitamin-c_100g")
    val vitaminC: Double = 0.0,
    @SerialName("vitamin-d2_100g")
    val vitaminD2: Double = 0.0,
    @SerialName("vitamin-d3_100g")
    val vitaminD3: Double = 0.0,
    @SerialName("vitamin-d_100g")
    val vitaminD: Double = 0.0,
    @SerialName("vitamin-e_100g")
    val vitaminE: Double = 0.0,
    @SerialName("vitamin-k_100g")
    val vitaminK: Double = 0.0,

    @SerialName("beta-carotene_100g")
    val betaCarotene: Double= 0.0,
    @SerialName("fluoride_100g")
    val fluoride: Double= 0.0,
    @SerialName("alpha-carotene_100g")
    val alphaCarotene: Double = 0.0,

    val water_100g: Double= 0.0,
    val zinc_100g: Double= 0.0

)

 */