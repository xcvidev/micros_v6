package com.xcvi.micros.data.repository

import android.util.Log
import androidx.room.Query
import com.xcvi.micros.data.repository.utils.mergeToFood
import com.xcvi.micros.data.repository.utils.toEntity
import com.xcvi.micros.data.repository.utils.toModel
import com.xcvi.micros.data.source.local.food.FoodDao
import com.xcvi.micros.data.source.remote.AiApi
import com.xcvi.micros.data.source.remote.ProductApi
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.fetchAndCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.distinctBy
import kotlin.collections.plus

class FoodRepositoryImplementation(
    private val foodDao: FoodDao,
    private val productApi: ProductApi,
    private val aiApi: AiApi
) : FoodRepository {

    override suspend fun create(food: Food, overwrite: Boolean): Response<Unit> {
        try {
            val entity = food.toEntity()
            if (overwrite) {
                foodDao.upsert(entity)
            } else {
                foodDao.create(entity)
            }
            return Response.Success(Unit)
        } catch (e: Exception) {
            Log.e("FoodRepository", "create: ", e)
            return Response.Error(Failure.AlreadyExists)
        }
    }



    override suspend fun search(query: String): Response<List<Food>> {
        return fetchAndCache(
            apiCall = {
                coroutineScope {
                    val en = async { productApi.search(query = query,  "en") }.await()
                    val it = async { productApi.search(query = query,  "it") }.await()
                    val fr = async { productApi.search(query = query,  "fr") }.await()
                    if (en != null && it != null && fr != null) {
                        (en + fr + it).distinctBy { it.barcode }
                    } else {
                        null
                    }
                }
            },
            cacheCall = { response ->
                val entities = response.map { it.toEntity() }
                foodDao.insert(entities)
            },
            dbCall = { response ->
                val barcodes = response.map { it.barcode }
                val remote = foodDao.get(barcodes).map { it.toModel() }
                val local = foodDao.search(query).map { it.toModel() }
                (local + remote).distinctBy { it.barcode }
            },
            fallbackRequest = null,
            fallbackDbCall = { foodDao.search(query).map { it.toModel() } }
        )
    }

    override suspend fun enhance(
        foodBarcode: String,
        description: String
    ): Response<Food> {
        val food = foodDao.get(foodBarcode) ?: return Response.Error(Failure.EmptyResult)
        //val newBarcode = "AI_${food.barcode}"
        val newBarcode = food.barcode
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
        return try {
            val local = foodDao.get(barcode)
            if (local != null) {
                Response.Success(local.toModel())
            } else {
                fetchAndCache(
                    apiCall = { productApi.scan(barcode) },
                    cacheCall = { response ->
                        val entity = response.toEntity()
                        if (entity != null) {
                            foodDao.insert(entity)
                        }
                    },
                    dbCall = { foodDao.get(barcode)?.toModel() },
                    fallbackRequest = null,
                    fallbackDbCall = { null }
                )
            }
        } catch (e: Exception) {
            Log.e("FoodRepository", "scan: ", e)
            Response.Error(Failure.Network)
        }
    }

    override suspend fun getRecents(): List<Food> {
        return foodDao.getRecents().map { list -> list.toModel() }
    }

    override suspend fun getFavorites(): List<Food> {
        return foodDao.getFavorites().map { list -> list.toModel() }
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