package com.xcvi.micros.data.repository
import com.xcvi.micros.data.source.local.entity.weight.WeightEntity
import com.xcvi.micros.data.source.local.weight.WeightDao
import com.xcvi.micros.domain.model.weight.Weight
import com.xcvi.micros.domain.model.weight.WeightSummary
import com.xcvi.micros.domain.model.weight.WeightUnit
import com.xcvi.micros.domain.respostory.WeightRepository
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getLocalDateTime
import com.xcvi.micros.domain.utils.getNow
import com.xcvi.micros.domain.utils.getTimestamp
import com.xcvi.micros.domain.utils.roundDecimals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WeightRepositoryImplementation(
    private val dao: WeightDao
) : WeightRepository {


    override suspend fun changeUnit(unit: WeightUnit): Response<Unit> {
        return try {
            dao.convertAll(unit.name)
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    override suspend fun getHistory(): List<WeightSummary> {
        return try {
            withContext(Dispatchers.IO) {
                val weights = dao.getHistory()
                weights.map { stat ->
                    WeightSummary(
                        date = stat.date,
                        min = stat.min,
                        max = stat.max,
                        avg = stat.avg,
                        unit = WeightUnit.valueOf(stat.unit),
                        label = ""
                    )

                }
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getLatest(): Response<Weight> {
        return try {
            val weight = dao.getLatest() ?: return Response.Success(Weight.empty)
            val model = Weight(
                weight = weight.weight,
                timestamp = weight.timestamp,
                date = weight.date,
                unit = WeightUnit.valueOf(weight.unit)
            )
            Response.Success(model)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    override suspend fun getWeights(start: Int, end: Int): Flow<List<Weight>> {
        return try {
            dao.getWeights(start, end).map { list ->
                list.map {
                    Weight(
                        weight = it.weight,
                        timestamp = it.timestamp,
                        date = it.date,
                        unit = WeightUnit.valueOf(it.unit)
                    )
                }
            }
        } catch (e: Exception) {
            emptyFlow()
        }
    }

    override suspend fun save(value: Double, date: Int, unit: WeightUnit): Response<Unit> {
        if (value <= 0.0) return Response.Error(Failure.InvalidInput)
        return try {
            val time = getNow().getLocalDateTime()
            val weight = WeightEntity(
                weight = value.roundDecimals(),
                timestamp = date.getTimestamp(time.hour, time.minute, time.second),
                date = date,
                unit = unit.name
            )
            dao.upsert(weight)
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    override suspend fun delete(weight: Weight): Response<Unit> {
        return try {
            dao.delete(weight.timestamp)
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }
}