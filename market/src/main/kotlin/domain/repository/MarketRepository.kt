package domain.repository

import domain.model.MarketAllergen
import domain.model.MarketFilter
import domain.model.MarketPage
import domain.model.MarketProduct
import domain.model.MarketResult
import domain.model.MarketSeller

interface MarketRepository {
    suspend fun getProducts(filter: MarketFilter, page: Int): MarketResult<MarketPage<MarketProduct>>
    suspend fun getProduct(productId: String): MarketResult<MarketProduct>
    suspend fun getSeller(sellerId: String): MarketResult<MarketSeller>
    suspend fun getSellerProducts(sellerId: String, page: Int): MarketResult<MarketPage<MarketProduct>>
    suspend fun getAllergens(): MarketResult<List<MarketAllergen>>
}
