package com.xcvi.micros.data.source.local.entity.food.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.xcvi.micros.data.source.local.entity.food.FoodEntity
import com.xcvi.micros.data.source.local.entity.food.PortionEntity

data class PortionWithFood(
    @Embedded val portion: PortionEntity,
    @Relation(
        parentColumn = "barcode",
        entityColumn = "barcode"
    )
    val food: FoodEntity
)