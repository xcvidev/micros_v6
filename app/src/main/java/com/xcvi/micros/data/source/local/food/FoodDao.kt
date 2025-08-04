package com.xcvi.micros.data.source.local.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcvi.micros.data.source.local.entity.food.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao{
    /**
     * Get
     */
    @Query("SELECT * FROM FoodEntity WHERE barcode = :barcode LIMIT 1")
    suspend fun get(barcode: String): FoodEntity?

    @Query("SELECT * FROM FoodEntity WHERE barcode IN (:barcodes)")
    suspend fun get(barcodes: List<String>): List<FoodEntity>

    @Query("SELECT * FROM FoodEntity WHERE isFavorite = 1 OR isRecent = 1 ORDER BY isFavorite, name")
    fun getRecents(): Flow<List<FoodEntity>>

    @RawQuery
    suspend fun search(query: SupportSQLiteQuery): List<FoodEntity>

    /**
     * Update
     */
    @Query("UPDATE FoodEntity SET isFavorite = NOT isFavorite WHERE barcode = :barcode")
    suspend fun setFavorite(barcode: String)

    @Query("UPDATE FoodEntity SET isFavorite = :value WHERE barcode = :barcode")
    suspend fun setFavorite(barcode: String, value: Boolean)

    @Query("UPDATE FoodEntity SET isRecent = :value WHERE barcode = :barcode")
    suspend fun setRecent(barcode: String, value: Boolean)

    @Query("UPDATE FoodEntity SET name = :newName WHERE barcode = :barcode")
    suspend fun rename(barcode: String, newName: String)


    /**
     * Insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(food: FoodEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(foods: List<FoodEntity>)

    @Upsert
    suspend fun upsert(food: FoodEntity)

    @Upsert
    suspend fun upsert(foods: List<FoodEntity>)
}