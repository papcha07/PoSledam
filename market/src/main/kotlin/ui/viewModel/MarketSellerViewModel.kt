package ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.MarketInteractor
import domain.model.MarketResult
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.model.MarketSellerUiState

class MarketSellerViewModel(
    private val interactor: MarketInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(MarketSellerUiState())
    val state: StateFlow<MarketSellerUiState> = _state.asStateFlow()

    private var sellerId: String? = null
    private var currentPage = 0

    fun load(id: String) {
        sellerId = id
        currentPage = 0
        viewModelScope.launch {
            _state.value = MarketSellerUiState(isLoading = true)

            val sellerDeferred = async { interactor.getSeller(id) }
            val productsDeferred = async { interactor.getSellerProducts(id, page = 0) }

            val sellerResult = sellerDeferred.await()
            val productsResult = productsDeferred.await()

            if (sellerResult is MarketResult.Success && productsResult is MarketResult.Success) {
                currentPage = productsResult.data.page
                _state.value = MarketSellerUiState(
                    seller = sellerResult.data,
                    products = productsResult.data.items,
                    isLastPage = productsResult.data.isLast
                )
            } else {
                val code = (sellerResult as? MarketResult.Error)?.code
                    ?: (productsResult as? MarketResult.Error)?.code
                    ?: 400
                _state.value = MarketSellerUiState(errorMessage = marketErrorText(code))
            }
        }
    }

    fun loadNextPage() {
        val id = sellerId ?: return
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || current.isLastPage) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, errorMessage = null) }

            when (val result = interactor.getSellerProducts(id, currentPage + 1)) {
                is MarketResult.Error -> _state.update {
                    it.copy(isLoadingMore = false, errorMessage = marketErrorText(result.code))
                }

                is MarketResult.Success -> {
                    currentPage = result.data.page
                    _state.update {
                        it.copy(
                            products = it.products + result.data.items,
                            isLoadingMore = false,
                            isLastPage = result.data.isLast
                        )
                    }
                }
            }
        }
    }
}
