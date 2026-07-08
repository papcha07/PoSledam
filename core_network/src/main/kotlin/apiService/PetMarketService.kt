package apiService

import ApiResponse
import apiService.models.petmarket_models.AllergenResponse
import apiService.models.petmarket_models.AnimalTypeResponse
import apiService.models.petmarket_models.PageResponse
import apiService.models.petmarket_models.ProductCategoryResponse
import apiService.models.petmarket_models.ProductResponse
import apiService.models.petmarket_models.SellerResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import toApiErrorCode

class PetMarketService(private val client: HttpClient) {

    suspend fun getProducts(
        category: ProductCategoryResponse?,
        animalType: AnimalTypeResponse?,
        search: String?,
        excludeAllergenIds: List<String>,
        page: Int,
        size: Int = PAGE_SIZE
    ): ApiResponse<PageResponse<ProductResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = client.get {
                marketUrl("products")
                category?.let { parameter("category", it.name) }
                animalType?.let { parameter("animalType", it.name) }
                search?.takeIf { it.isNotBlank() }?.let { parameter("search", it) }
                excludeAllergenIds.forEach { parameter("excludeAllergenIds", it) }
                parameter("page", page)
                parameter("size", size)
                parameter("sort", "createdAt,desc")
            }

            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun getProduct(productId: String): ApiResponse<ProductResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.get {
                marketUrl("products", productId)
            }

            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun getSeller(sellerId: String): ApiResponse<SellerResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.get {
                marketUrl("sellers", sellerId)
            }

            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun getSellerProducts(
        sellerId: String,
        page: Int,
        size: Int = PAGE_SIZE
    ): ApiResponse<PageResponse<ProductResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = client.get {
                marketUrl("sellers", sellerId, "products")
                parameter("page", page)
                parameter("size", size)
                parameter("sort", "createdAt,desc")
            }

            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun getAllergens(): ApiResponse<List<AllergenResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = client.get {
                marketUrl("allergens")
            }

            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    private fun io.ktor.client.request.HttpRequestBuilder.marketUrl(vararg segments: String) {
        url {
            protocol = URLProtocol.HTTP
            host = MARKET_HOST
            port = MARKET_PORT
            path("api", "v1", *segments)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
        private const val MARKET_HOST = "161.104.52.29"
        private const val MARKET_PORT = 8081
    }
}
