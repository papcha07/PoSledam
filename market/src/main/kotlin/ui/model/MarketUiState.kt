package ui.model

import domain.model.MarketAllergen
import domain.model.MarketAnimalType
import domain.model.MarketCategory
import domain.model.MarketProduct
import domain.model.MarketSeller

data class MarketCatalogUiState(
    val products: List<MarketProduct> = emptyList(),
    val allergens: List<MarketAllergen> = emptyList(),
    val selectedCategory: MarketCategory? = null,
    val selectedAnimalType: MarketAnimalType? = null,
    val searchQuery: String = "",
    val withoutAllergens: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
)

sealed interface MarketProductDetailsUiState {
    data object Loading : MarketProductDetailsUiState
    data class Success(val product: MarketProduct) : MarketProductDetailsUiState
    data class Error(val message: String) : MarketProductDetailsUiState
}

data class MarketSellerUiState(
    val seller: MarketSeller? = null,
    val products: List<MarketProduct> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
)
