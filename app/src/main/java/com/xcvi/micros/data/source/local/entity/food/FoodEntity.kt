package com.xcvi.micros.data.source.local.entity.food
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class FoodEntity(
    @PrimaryKey(autoGenerate = false)
    val barcode: String,

    val name: String,

    val isFavorite: Boolean,
    val isRecent: Boolean,
    val isAI: Boolean,

    val tag: String,
    val tagwordcount: Int,

    /**
     * Nutrients per 100g
     */
    val calories: Double,
    val protein: Double,
    val carbohydrates: Double,
    val fats: Double,
    val saturatedFats: Double,
    val fiber: Double,
    val sugars: Double,
    /**
     * Micro Nutrients per 100g
     */
    val calcium: Double, //mg
    val iron: Double,  //mg
    val magnesium: Double, //mg
    val potassium: Double, //mg
    val sodium: Double, //mg
    val zinc: Double,
    val fluoride: Double,
    val iodine: Double,
    val phosphorus: Double,
    val manganese: Double,
    val selenium: Double,

    val vitaminA: Double, //mcg
    val vitaminB1: Double, //mg
    val vitaminB2: Double, //mg
    val vitaminB3: Double, //mg
    val vitaminB4: Double, //mg
    val vitaminB5: Double, //mg
    val vitaminB6: Double, //mg
    val vitaminB9: Double, //mg
    val vitaminB12: Double, //mcg
    val vitaminC: Double, //mg
    val vitaminD: Double, //mcg
    val vitaminE: Double, //mg
    val vitaminK: Double, //mcg

    val histidine: Double,
    val isoleucine: Double,
    val leucine: Double,
    val lysine: Double,
    val methionine: Double,
    val phenylalanine: Double,
    val threonine: Double,
    val tryptophan: Double,
    val valine: Double
)