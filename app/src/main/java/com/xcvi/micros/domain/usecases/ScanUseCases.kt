package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.utils.Response

class ScanUseCases(
    private val foodRepository: FoodRepository
){
    suspend fun scan(barcode: String):  Response<Food> {
        return  when(val res =foodRepository.scan(barcode)){
            is Response.Error -> {
                Response.Error(res.error)
            }
            is Response.Success -> {
                Response.Success(res.data)
            }
        }
    }
}