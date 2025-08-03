package com.xcvi.micros.domain.respostory

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    suspend fun scan(barcode: String): Response<Food>
    suspend fun search(query: String, language: String): Response<List<Food>>
    suspend fun enhance(foodBarcode: String, description: String): Response<Food>
    suspend fun toggleFavorite(barcode: String): Response<Unit>
    suspend fun setRecent(barcode: String, value: Boolean): Response<Unit>
    suspend fun rename(newName: String, barcode: String): Response<Unit>
    fun getRecents(): Flow<List<Food>>
    suspend fun getFood(barcode: String): Response<Food>
}