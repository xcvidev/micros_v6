package com.xcvi.micros.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xcvi.micros.data.source.local.entity.food.FoodEntity
import com.xcvi.micros.data.source.local.entity.food.MacroGoalEntity
import com.xcvi.micros.data.source.local.entity.food.PortionEntity
import com.xcvi.micros.data.source.local.entity.message.FoodItemEntity
import com.xcvi.micros.data.source.local.entity.message.MessageEntity
import com.xcvi.micros.data.source.local.entity.weight.WeightEntity
import com.xcvi.micros.data.source.local.food.FoodDao
import com.xcvi.micros.data.source.local.food.PortionDao
import com.xcvi.micros.data.source.local.message.MessageDao
import com.xcvi.micros.data.source.local.weight.WeightDao

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        FoodEntity::class,
        PortionEntity::class,
        MacroGoalEntity::class,

        MessageEntity::class,
        FoodItemEntity::class,

        WeightEntity::class,
    ]
)
abstract class MicrosDatabase: RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun portionDao(): PortionDao
    abstract fun weightDao(): WeightDao
    abstract fun messageDao(): MessageDao
}


