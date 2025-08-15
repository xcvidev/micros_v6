package com.xcvi.micros.data.source.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN calcium REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN iron REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN magnesium REAL NOT NULL DEFAULT 0.0")

        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminA REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminB1 REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminB2 REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminB3 REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminB4 REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminB5 REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminB6 REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminB9 REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminB12 REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminC REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminD REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminE REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN vitaminK REAL NOT NULL DEFAULT 0.0")

        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN histidine REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN isoleucine REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN leucine REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN lysine REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN methionine REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN phenylalanine REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN threonine REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN tryptophan REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE FoodItemEntity ADD COLUMN valine REAL NOT NULL DEFAULT 0.0")
    }
}
