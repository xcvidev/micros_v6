package com.xcvi.micros.data.repository

import android.util.Log
import com.xcvi.micros.data.repository.utils.getMacros
import com.xcvi.micros.data.repository.utils.mergeActualWithGoals
import com.xcvi.micros.data.repository.utils.toEntity
import com.xcvi.micros.data.repository.utils.toModel
import com.xcvi.micros.data.source.local.entity.food.MacroGoalEntity
import com.xcvi.micros.data.source.local.entity.food.PortionEntity
import com.xcvi.micros.data.source.local.food.PortionDao
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Meal
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.sumAminos
import com.xcvi.micros.domain.model.food.sumMinerals
import com.xcvi.micros.domain.model.food.sumNutrients
import com.xcvi.micros.domain.model.food.sumVitamins
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PortionRepositoryImplementation(
    private val portionDao: PortionDao,
) : PortionRepository {

    override suspend fun getMeals(date: Int, mealNames: Map<Int, String>): Flow<List<Meal>> {
        return try {
            withContext(Dispatchers.IO) {
                combine(
                    getPortionsOfDate(date),            // Flow<List<Portion>>
                    UserPreferences.favoritesFlow()         // Flow<Set<Int>>
                ) { allPortions, favs ->
                    val byMeal = allPortions.groupBy { it.meal }
                    (1..8).map { i ->
                        val portions = byMeal[i].orEmpty()
                        val fav = i in favs
                        Meal(
                            date = date,
                            number = i,
                            name = mealNames[i] ?: "",
                            portions = portions,
                            isFavorite = fav,
                            isVisible = portions.isNotEmpty() || fav,
                            nutrients = portions.sumNutrients(),
                            minerals = portions.sumMinerals(),
                            vitamins = portions.sumVitamins(),
                            aminoAcids = portions.sumAminos()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            return emptyFlow()
        }
    }

    override fun getPortionsOfDate(date: Int): Flow<List<Portion>> {
        return portionDao.getPortions(date = date)
            .map { listOfEntities ->
                listOfEntities.map { it.toModel() }
            }
            .catch { emit(emptyList()) }
    }

    override fun getPortionsOfMeal(date: Int, meal: Int): Flow<List<Portion>> {
        return portionDao.getPortions(date = date, meal = meal)
            .map { listOfEntities ->
                listOfEntities.map { it.toModel() }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun getPortion(barcode: String, date: Int, meal: Int): Response<Portion> {
        return try {
            val res =
                withContext(Dispatchers.IO) {
                    portionDao.getPortion(
                        barcode = barcode,
                        date = date,
                        meal = meal
                    )
                }
            if (res != null) {
                withContext(Dispatchers.Default) { Response.Success(res.toModel()) }
            } else {
                Response.Error(Failure.Database)
            }
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    /**
     * Save
     */
    override suspend fun savePortions(
        date: Int,
        meal: Int,
        barcodes: List<String>,
    ): Response<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val portions = barcodes.map { barcode ->
                    PortionEntity(
                        barcode = barcode,
                        date = date,
                        meal = meal,
                        amount = 100.0
                    )
                }
                portionDao.upsert(portions)
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    override suspend fun savePortion(
        amount: Int,
        date: Int,
        meal: Int,
        barcode: String,
    ): Response<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                portionDao.upsert(
                    PortionEntity(
                        barcode = barcode,
                        date = date,
                        meal = meal,
                        amount = amount.toDouble()
                    )
                )
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    override suspend fun copyPortions(list: List<Portion>, newDate: Int, newMeal: Int) = try {
        withContext(Dispatchers.IO) {
            portionDao.upsert(list.map { it.toEntity().copy(date = newDate, meal = newMeal) })
        }
        Response.Success(Unit)
    } catch (e: Exception) {
        Response.Error(Failure.Database)
    }


    override suspend fun deletePortion(barcode: String, date: Int, meal: Int) = try {
        withContext(Dispatchers.IO) {
            portionDao.delete(barcode = barcode, date = date, meal = meal)
        }
        Response.Success(Unit)
    } catch (e: Exception) {
        Response.Error(Failure.Database)
    }

    override suspend fun deletePortions(date: Int, meal: Int) = try {
        withContext(Dispatchers.IO) {
            portionDao.delete(date = date, meal = meal)
        }
        Response.Success(Unit)
    } catch (e: Exception) {
        Response.Error(Failure.Database)
    }

    /**
     * Stats
     */
    override suspend fun getAllSummaries(): List<MacrosSummary> {
        try {
            val actualList = withContext(Dispatchers.IO) { portionDao.getHistory() }
            val goalsList = withContext(Dispatchers.IO) { portionDao.getGoals() }
            return withContext(Dispatchers.Default) {
                mergeActualWithGoals(actualList, goalsList)
            }
        } catch (e: Exception) {
            Log.e("log", "getAllSummaries: ", e)
            return emptyList()
        }
    }

    override suspend fun observeAllSummaries(): Flow<List<MacrosSummary>> {
        try {
            val actualList = withContext(Dispatchers.IO) { portionDao.observeHistory() }
            val goalsList = withContext(Dispatchers.IO) { portionDao.observeGoals() }
            return withContext(Dispatchers.Default) {
                combine(actualList, goalsList) { actual, goals ->
                    mergeActualWithGoals(actual, goals)
                }
            }
        } catch (e: Exception) {
            return emptyFlow()
        }
    }

    override suspend fun getSummaryOfDate(date: Int): Flow<MacrosSummary> {
        try {
            val actual = withContext(Dispatchers.IO) { portionDao.observeMacrosForDate(date) }
            val goal = withContext(Dispatchers.IO) { portionDao.getGoal(date) }
            return withContext(Dispatchers.Default) {
                actual.map { actualMacros ->
                    MacrosSummary(
                        date = date,
                        actual = actualMacros.getMacros(),
                        goal = goal.getMacros()
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("log", "getSummaryOfDate: ", e)
            return emptyFlow()
        }
    }


    override suspend fun saveGoals(
        protein: Int,
        carbs: Int,
        fats: Int
    ): Response<Unit> {
        try {
            portionDao.upsert(
                MacroGoalEntity(
                    date = getToday(),
                    calories = protein * 4.0 + carbs * 4 + fats * 9,
                    protein = protein.toDouble(),
                    carbohydrates = carbs.toDouble(),
                    fats = fats.toDouble()
                )
            )
            return Response.Success(Unit)
        } catch (e: Exception) {
            return Response.Error(Failure.Database)
        }
    }
}
