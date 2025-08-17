package com.xcvi.micros.data.source.remote

import com.xcvi.micros.data.source.remote.dto.ScanDTO
import com.xcvi.micros.data.source.remote.dto.ScanProductDTO
import com.xcvi.micros.data.source.remote.dto.SearchDTO
import com.xcvi.micros.data.source.remote.dto.SearchProductDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url


class ProductApi(
    private val client: HttpClient
) {
    /**
     * https://search.openfoodfacts.org/docs
     */
    suspend fun search(
        query: String,
        language: String,
        page: Int = 1,
        pageSize: Int = 50
    ): List<SearchProductDTO>? {
        val url = "https://search.openfoodfacts.org/search"

        val res: SearchDTO? = client.get {
            url(url)
            parameter("q", query)
            parameter("langs", language)
            parameter("page", page)
            parameter("page_size", pageSize)
            parameter("fields", "code,product_name,nutriments,brands")
        }
        return res?.hits
    }

    suspend fun scan(barcode: String): ScanProductDTO? {
        val url = "https://world.openfoodfacts.org/api/v3/product/$barcode"
        val res: ScanDTO = client.get(url)
        return res.product
    }
}
