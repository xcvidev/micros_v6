package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response

class ScanUseCases(
    private val foodRepository: FoodRepository,
    private val portionRepository: PortionRepository
){
    suspend fun scan(barcode: String):  Response<Food> {
        return  when(val res = foodRepository.scan(barcode)){
            is Response.Error -> {
                Response.Error(res.error)
            }
            is Response.Success -> {
                Response.Success(res.data)
            }
        }
    }


    suspend fun toggleFavorite(barcode: String): Response<Unit> {
        return foodRepository.toggleFavorite(barcode)
    }

    suspend fun enhance(barcode: String, description: String): Response<Food> {
        return foodRepository.enhance(barcode, description)
    }
    suspend fun updatePortion(
        newAmount: Int,
        date: Int,
        meal: Int,
        barcode: String,
    ): Response<Unit> {
        return portionRepository.savePortion(
            amount = newAmount,
            date = date,
            meal = meal,
            barcode = barcode
        )
    }
}