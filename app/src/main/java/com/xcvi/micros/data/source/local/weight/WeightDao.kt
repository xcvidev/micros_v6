package com.xcvi.micros.data.source.local.weight


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.xcvi.micros.data.source.local.entity.weight.WeightEntity
import com.xcvi.micros.data.source.local.entity.weight.WeightSummaryEntity
import com.xcvi.micros.domain.utils.Failure
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Query("SELECT * FROM WeightEntity ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): WeightEntity?

    @Query("SELECT MIN(weight) FROM WeightEntity ")
    suspend fun getMin(): Double?

    @Query("SELECT * FROM WeightEntity WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getWeights(startDate: Int, endDate: Int): Flow<List<WeightEntity>>

    @Upsert
    suspend fun upsert(weight: WeightEntity)

    @Query("DELETE FROM WeightEntity WHERE timestamp = :timestamp")
    suspend fun delete(timestamp: Long)


    @Query("""
        SELECT 
            date,
            MIN(unit) AS unit,
            AVG(weight) AS avg,
            MIN(weight) AS min,
            MAX(weight) AS max
        FROM WeightEntity
        GROUP BY date
        ORDER BY date DESC
    """)
    fun getHistory(): List<WeightSummaryEntity>

    @Query("SELECT MIN(weight) FROM WeightEntity")
    fun getMinimumWeight(): Double?



    /** Unit change **/
    @Query("SELECT * FROM WeightEntity")
    suspend fun getAllWeights(): List<WeightEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(weights: List<WeightEntity>)

    @Transaction
    suspend fun convertAll(targetUnit: String) {
        val current = getAllWeights()

        val converted = current.map { weight ->
            val convertedValue = when {
                weight.unit == targetUnit -> weight.weight // No change
                weight.unit == "kg" && targetUnit == "lbs" -> weight.weight * 2.20462
                weight.unit == "lbs" && targetUnit == "kg" -> weight.weight / 2.20462
                else -> throw Failure.Database
            }

            weight.copy(weight = convertedValue, unit = targetUnit)
        }

        upsertAll(converted)
    }

}