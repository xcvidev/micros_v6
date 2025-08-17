package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.MessageRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response

class SearchUseCases(
    private val foodRepository: FoodRepository,
    private val portionRepository: PortionRepository,
    private val messageRepository: MessageRepository
) {
    suspend fun enhance(barcode: String, description: String): Response<Food> {
        return foodRepository.enhance(foodBarcode = barcode, description)
    }

    suspend fun toggleFavorite(barcode: String): Response<Unit> {
        return foodRepository.toggleFavorite(barcode)
    }

    suspend fun smartSearch(query: String, language: String): Response<Message> {
        return messageRepository.smartSearch(userInput = query, language = language)
    }

    suspend fun scan(barcode: String, date: Int, meal: Int):  Response<Portion> {
        return when(val res = foodRepository.scan(barcode)){
            is Response.Error -> Response.Error(res.error)
            is Response.Success -> {
                val portion = res.data.scaleToPortion(
                    date = date,
                    meal = meal,
                    portionAmount = 100
                )
                Response.Success(portion)
            }
        }
    }

    suspend fun eat(
        date: Int,
        meal: Int,
        portions: Set<Portion>
    ): Response<Unit> {
        val toSave = portions.map {
            //Important for when saving recents/ai results with different dates and meal numbers
            it.copy(date = date, meal = meal)
        }
        return portionRepository.savePortions(toSave)
    }

    suspend fun getRecents(): List<Portion> {
        val portions = portionRepository.getRecents()
        val foods = foodRepository.getFavorites()
            .filter{ f ->
                portions.none { p -> p.food.barcode == f.barcode }
            }
            .map { food->
                Portion(
                    date = 0,
                    meal = 0,
                    food = food,
                    amount = 100
                )
        }

        return (portions + foods).sortedWith(
            compareByDescending<Portion> { it.food.isFavorite }
                .thenBy { it.food.name }
        )
    }

    suspend fun search(
        query: String,
        date: Int,
        meal: Int,
        language: String
    ): Response<List<Portion>> {
        val res = foodRepository.search(query)
        return when(res){
            is Response.Error -> res
            is Response.Success -> {
                val portions = res.data.map {
                    it.scaleToPortion(
                        date = date,
                        meal = meal,
                        portionAmount = 100
                    )
                }
                Response.Success(portions)
            }
        }
    }

}













