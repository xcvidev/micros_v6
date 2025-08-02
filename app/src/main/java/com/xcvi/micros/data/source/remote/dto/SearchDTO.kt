package com.xcvi.micros.data.source.remote.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchDTO(
    val page: Int = 1,
    val page_size: Int = 100,
    val hits: List<SearchProductDTO> = emptyList()
)

@Serializable
data class SearchProductDTO(
    @SerialName("code") val barcode: String = "",
    @SerialName("brands") val brands: List<String> = emptyList(),
    @SerialName("product_name") val name: String = "",
    @SerialName("nutriments") val nutriments: NutrimentsDTO = NutrimentsDTO()
)