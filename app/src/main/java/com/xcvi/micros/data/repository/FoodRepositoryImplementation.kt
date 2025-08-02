package com.xcvi.micros.data.repository

import android.util.Log
import com.xcvi.micros.data.repository.utils.mergeToFood
import com.xcvi.micros.data.repository.utils.toEntity
import com.xcvi.micros.data.repository.utils.toModel
import com.xcvi.micros.data.source.local.food.FoodDao
import com.xcvi.micros.data.source.remote.AiApi
import com.xcvi.micros.data.source.remote.ProductApi
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.fetchAndCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FoodRepositoryImplementation(
    private val foodDao: FoodDao,
    private val productApi: ProductApi,
    private val aiApi: AiApi
) : FoodRepository {

    override suspend fun search(
        searchTerm: String,
        language: String
    ): Response<List<Food>> {
        return fetchAndCache(
            apiCall = {
                productApi.search(searchTerm, language)
            },
            cacheCall = { response ->
                val entities = response.map { it.toEntity() }
                foodDao.insert(entities)
            },
            dbCall = { response ->
                val barcodes = response.map { it.barcode }
                foodDao.get(barcodes).map { it.toModel() }
            },
            fallbackRequest = null,
            fallbackDbCall = { null }
        )
    }

    override suspend fun enhance(
        foodBarcode: String,
        description: String
    ): Response<Food> {
        val food = foodDao.get(foodBarcode) ?: return Response.Error(Failure.EmptyResult)
        val newBarcode = "AI_${food.barcode}"
        return fetchAndCache(
            apiCall = {
                aiApi.enhance(
                    name = food.name,
                    ingredients = "",
                    userDesc = description,
                )
            },
            cacheCall = { response ->
                val enhancedFood = response.mergeToFood(food, newBarcode)
                foodDao.upsert(enhancedFood)
            },
            dbCall = {
                foodDao.get(newBarcode)?.toModel()
            },
            fallbackRequest = null,
            fallbackDbCall = { null }
        )
    }

    override suspend fun scan(barcode: String): Response<Food> {
        val res = fetchAndCache(
            apiCall = { productApi.scan(barcode) },
            cacheCall = { response ->
                val entity = response.toEntity()
                if (entity != null) {
                    foodDao.insert(entity)
                }
            },
            dbCall = { foodDao.get(barcode) },
            fallbackRequest = null,
            fallbackDbCall = { null }
        )

        return when (res) {
            is Response.Success -> Response.Success(res.data.toModel())
            is Response.Error -> {
                Log.e("FoodRepository", "scan: ", res.error)
                Response.Error(res.error)
            }
        }
    }

    override fun getRecents(): Flow<List<Food>> {
        return foodDao.getRecents()
            .map { list -> list.map { it.toModel() } }
            .catch { e -> Log.e("FoodRepository", "getRecents: ", e) }
    }

    override suspend fun getFood(barcode: String): Response<Food> {
        try {
            val food = withContext(Dispatchers.IO) {
                foodDao.get(barcode)
            }
            if (food != null) {
                return Response.Success(food.toModel())
            } else {
                return Response.Error(Failure.EmptyResult)
            }
        } catch (e: Exception) {
            Log.e("FoodRepository", "getFood: ", e)
            return Response.Error(Failure.Database)
        }
    }


    override suspend fun toggleFavorite(barcode: String): Response<Unit> {
        try {
            withContext(Dispatchers.IO) { foodDao.setFavorite(barcode) }
            return Response.Success(Unit)
        } catch (e: Exception) {
            Log.e("FoodRepository", "toggleFavorite: ", e)
            return Response.Error(Failure.Database)
        }
    }

    override suspend fun setRecent(
        barcode: String,
        value: Boolean
    ): Response<Unit> {
        try {
            withContext(Dispatchers.IO) { foodDao.setRecent(barcode, value) }
            return Response.Success(Unit)
        } catch (e: Exception) {
            Log.e("FoodRepository", "setRecent: ", e)
            return Response.Error(Failure.Database)
        }
    }

    override suspend fun rename(
        newName: String,
        barcode: String
    ): Response<Unit> {
        try {
            withContext(Dispatchers.IO) { foodDao.rename(barcode, newName) }
            return Response.Success(Unit)
        } catch (e: Exception) {
            Log.e("FoodRepository", "rename: ", e)
            return Response.Error(Failure.Database)
        }
    }


}