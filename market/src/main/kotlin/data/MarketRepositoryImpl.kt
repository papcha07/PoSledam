package data

import ApiResponse
import apiService.PetMarketService
import domain.model.MarketAllergen
import domain.model.MarketFilter
import domain.model.MarketPage
import domain.model.MarketProduct
import domain.model.MarketResult
import domain.model.MarketSeller
import domain.repository.MarketRepository

class MarketRepositoryImpl(
    private val service: PetMarketService
) : MarketRepository {
    override suspend fun getProducts(
        filter: MarketFilter,
        page: Int
    ): MarketResult<MarketPage<MarketProduct>> {
        if (USE_MOCK_DATA) {
            return MarketResult.Success(MarketMockData.products(filter, page))
        }

        return when (
            val response = service.getProducts(
                category = filter.toCategoryResponse(),
                animalType = filter.toAnimalResponse(),
                search = filter.search,
                excludeAllergenIds = filter.excludeAllergenIds,
                page = page
            )
        ) {
            is ApiResponse.Error -> MarketResult.Error(response.errorCode)
            is ApiResponse.Success -> MarketResult.Success(
                MarketPage(
                    items = response.data.content.map { it.toMarketProduct() },
                    page = response.data.number,
                    isLast = response.data.last
                )
            )
        }
    }

    override suspend fun getProduct(productId: String): MarketResult<MarketProduct> =
        if (USE_MOCK_DATA) {
            MarketMockData.product(productId)?.let { MarketResult.Success(it) }
                ?: MarketResult.Error(404)
        } else {
        when (val response = service.getProduct(productId)) {
            is ApiResponse.Error -> MarketResult.Error(response.errorCode)
            is ApiResponse.Success -> MarketResult.Success(response.data.toMarketProduct())
        }
        }

    override suspend fun getSeller(sellerId: String): MarketResult<MarketSeller> =
        if (USE_MOCK_DATA) {
            MarketMockData.seller(sellerId)?.let { MarketResult.Success(it) }
                ?: MarketResult.Error(404)
        } else {
        when (val response = service.getSeller(sellerId)) {
            is ApiResponse.Error -> MarketResult.Error(response.errorCode)
            is ApiResponse.Success -> MarketResult.Success(response.data.toMarketSeller())
        }
        }

    override suspend fun getSellerProducts(
        sellerId: String,
        page: Int
    ): MarketResult<MarketPage<MarketProduct>> =
        if (USE_MOCK_DATA) {
            MarketResult.Success(MarketMockData.sellerProducts(sellerId, page))
        } else {
        when (val response = service.getSellerProducts(sellerId, page)) {
            is ApiResponse.Error -> MarketResult.Error(response.errorCode)
            is ApiResponse.Success -> MarketResult.Success(
                MarketPage(
                    items = response.data.content.map { it.toMarketProduct() },
                    page = response.data.number,
                    isLast = response.data.last
                )
            )
        }
        }

    override suspend fun getAllergens(): MarketResult<List<MarketAllergen>> =
        if (USE_MOCK_DATA) {
            MarketResult.Success(MarketMockData.allergens)
        } else {
        when (val response = service.getAllergens()) {
            is ApiResponse.Error -> MarketResult.Error(response.errorCode)
            is ApiResponse.Success -> MarketResult.Success(response.data.map { it.toMarketAllergen() })
        }
        }

    companion object {
        private const val USE_MOCK_DATA = true
    }
}
