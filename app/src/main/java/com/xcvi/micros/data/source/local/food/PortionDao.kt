package com.xcvi.micros.data.source.local.food

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.xcvi.micros.data.source.local.entity.food.*
import com.xcvi.micros.data.source.local.entity.food.relations.MacrosWithDate
import com.xcvi.micros.data.source.local.entity.food.relations.PortionWithFood
import kotlinx.coroutines.flow.Flow
@Dao
interface PortionDao{
    /**
     * Deletes
     */

    @Query("DELETE FROM PortionEntity WHERE date = :date AND meal = :meal")
    suspend fun delete(date: Int, meal: Int)

    @Query("DELETE FROM PortionEntity WHERE date = :date AND meal = :meal AND barcode = :barcode")
    suspend fun delete(barcode: String, date: Int, meal: Int)

    /**
     * Updates
     */
    @Upsert
    suspend fun upsert(portion: PortionEntity)

    @Upsert
    suspend fun upsert(portions: List<PortionEntity>)

    /**
     * Gets
     */

    @Transaction
    @Query("SELECT * FROM PortionEntity WHERE date > 0 ORDER BY date DESC")
    fun getPortions(): Flow<List<PortionWithFood>>

    @Transaction
    @Query("SELECT * FROM PortionEntity WHERE date = :date ORDER BY meal ASC")
    fun getPortions(date: Int): Flow<List<PortionWithFood>>

    @Transaction
    @Query("SELECT * FROM PortionEntity WHERE date = :date AND meal = :meal")
    fun getPortions(date: Int, meal: Int): Flow<List<PortionWithFood>>

    @Transaction
    @Query("SELECT * FROM PortionEntity WHERE barcode = :barcode AND date = :date AND meal = :meal ")
    suspend fun getPortion(
        barcode: String,
        date: Int,
        meal: Int
    ): PortionWithFood?

    @Query(
        """
    SELECT
        p.date AS date,
        SUM((f.calories * p.amount) / 100.0) AS calories,
        SUM((f.protein * p.amount) / 100.0) AS protein,
        SUM((f.carbohydrates * p.amount) / 100.0) AS carbohydrates,
        SUM((f.fats * p.amount) / 100.0) AS fats
    FROM PortionEntity AS p
    INNER JOIN FoodEntity AS f ON f.barcode = p.barcode
    WHERE p.date = :date
    GROUP BY p.date
    LIMIT 1
    """
    )
    fun observeMacrosForDate(date: Int): Flow<MacrosWithDate?>

    @Query("""
        SELECT * FROM MacroGoalEntity
        WHERE date <= :date
        ORDER BY date DESC
        LIMIT 1
    """)
    suspend fun getGoal(date: Int): MacrosWithDate?


    @Transaction
    @Query(
        """
    SELECT
        p.date AS date,
        SUM((f.calories * p.amount) / 100.0) AS calories,
        SUM((f.protein * p.amount) / 100.0) AS protein,
        SUM((f.carbohydrates * p.amount) / 100.0) AS carbohydrates,
        SUM((f.fats * p.amount) / 100.0) AS fats
    FROM PortionEntity AS p
    INNER JOIN FoodEntity AS f ON f.barcode = p.barcode
    WHERE p.date > 0
    GROUP BY p.date
    ORDER BY date DESC
    """
    )
    suspend fun getHistory(): List<MacrosWithDate>

    @Transaction
    @Query(
        """
    SELECT
        p.date AS date,
        SUM((f.calories * p.amount) / 100.0) AS calories,
        SUM((f.protein * p.amount) / 100.0) AS protein,
        SUM((f.carbohydrates * p.amount) / 100.0) AS carbohydrates,
        SUM((f.fats * p.amount) / 100.0) AS fats
    FROM PortionEntity AS p
    INNER JOIN FoodEntity AS f ON f.barcode = p.barcode
    WHERE p.date > 0
    GROUP BY p.date
    ORDER BY date DESC
    """
    )
    fun observeHistory(): Flow<List<MacrosWithDate>>


    @Query("SELECT * FROM MacroGoalEntity ORDER BY date DESC")
    suspend fun getGoals(): List<MacroGoalEntity>

    @Query("SELECT * FROM MacroGoalEntity ORDER BY date DESC")
    fun observeGoals(): Flow<List<MacroGoalEntity>>

    @Upsert
    suspend fun upsert(goal: MacroGoalEntity)
}