package domain.interactor

import domain.model.MarketAllergen
import domain.model.MarketFilter
import domain.model.MarketPage
import domain.model.MarketProduct
import domain.model.MarketResult
import domain.model.MarketSeller
import domain.repository.MarketRepository

class MarketInteractorImpl(
    private val repository: MarketRepository
) : MarketInteractor {
    override suspend fun getProducts(filter: MarketFilter, page: Int): MarketResult<MarketPage<MarketProduct>> =
        repository.getProducts(filter, page)

    override suspend fun getProduct(productId: String): MarketResult<MarketProduct> =
        repository.getProduct(productId)

    override suspend fun getSeller(sellerId: String): MarketResult<MarketSeller> =
        repository.getSeller(sellerId)

    override suspend fun getSellerProducts(sellerId: String, page: Int): MarketResult<MarketPage<MarketProduct>> =
        repository.getSellerProducts(sellerId, page)

    override suspend fun getAllergens(): MarketResult<List<MarketAllergen>> =
        repository.getAllergens()
}
