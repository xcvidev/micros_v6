package com.xcvi.micros.domain.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun <FallbackRequest, ApiResult, DbResult> fetchAndCache(
    apiCall: suspend () -> ApiResult?,
    cacheCall: suspend (ApiResult) -> Unit,
    dbCall: suspend (ApiResult) -> DbResult?,
    fallbackRequest: FallbackRequest,
    fallbackDbCall: suspend (FallbackRequest) -> DbResult?,
    isEmptyResult: (DbResult) -> Boolean = { false },
    notFoundFailure: Failure = Failure.EmptyResult,
    networkFailure: Failure = Failure.Network,
): Response<DbResult> = withContext(Dispatchers.IO) {

    val apiResponse: Response<ApiResult> = try {
        val result = apiCall()
        if (result != null) {
            Response.Success(result)
        } else {
            Response.Error(notFoundFailure)
        }
    } catch (e: Exception) {
        Response.Error(networkFailure)
    }

    return@withContext when (apiResponse) {
        is Response.Success -> {    // Might be Success with empty list
            val dbResult = try {
                cacheCall(apiResponse.data)
                dbCall(apiResponse.data)  // Might be null or empty list
            } catch (e: Exception) {
                null
            }

            if (dbResult != null && !isEmptyResult(dbResult)) { // Handle null or empty list
                Response.Success(dbResult)
            } else {
                /*
                returning notFound rather than db failure, because if api success but db error, it is most likley an empty api result (empty list)
                and not a database error. Even if a db error occurs, it is most likely fatal and user can't do anything about it.
                 */
                Response.Error(notFoundFailure)
            }
        }

        is Response.Error -> {
            val fallbackResult = try {
                fallbackDbCall(fallbackRequest)
            } catch (e: Exception) {
                null
            }

            return@withContext if (fallbackResult != null && !isEmptyResult(fallbackResult)) {
                Response.Success(fallbackResult)
            } else {
                apiResponse // return the original API error here
            }
        }
    }
}