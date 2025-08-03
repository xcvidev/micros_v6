package com.xcvi.micros.domain.respostory

import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Meal
import com.xcvi.micros.domain.model.food.Portion
import kotlinx.coroutines.flow.Flow

interface PortionRepository {

    fun observeAllSummaries(): Flow<List<MacrosSummary>>
    suspend fun getAllSummaries(): List<MacrosSummary>

     fun getSummaryOfDate(date: Int): Flow<MacrosSummary>

     fun getMeals(date: Int, mealNames: Map<Int,String>): Flow<List<Meal>>
     fun getRecents(): Flow<List<Portion>>

    suspend fun getPortion(barcode: String, date: Int, meal: Int): Response<Portion>

    fun getPortionsOfMeal(date: Int, meal: Int): Flow<List<Portion>>

    fun getPortionsOfDate(date: Int): Flow<List<Portion>>

    suspend fun saveGoals(protein: Int, carbs: Int, fats: Int): Response<Unit>

    suspend fun savePortion(amount: Int, date: Int, meal: Int, barcode: String): Response<Unit>

    suspend fun savePortions(portions: List<Portion>): Response<Unit>

    suspend fun copyPortions(list: List<Portion>, newDate: Int, newMeal: Int) : Response<Unit>

    suspend fun deletePortion(barcode: String, date: Int, meal: Int): Response<Unit>

    suspend fun deletePortions(date: Int, meal: Int): Response<Unit>
}