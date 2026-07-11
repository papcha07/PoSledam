package ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.MarketInteractor
import domain.model.MarketResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.model.MarketProductDetailsUiState

class MarketProductDetailsViewModel(
    private val interactor: MarketInteractor
) : ViewModel() {
    private val _state = MutableStateFlow<MarketProductDetailsUiState>(MarketProductDetailsUiState.Loading)
    val state: StateFlow<MarketProductDetailsUiState> = _state.asStateFlow()

    fun load(productId: String) {
        viewModelScope.launch {
            _state.value = MarketProductDetailsUiState.Loading
            _state.value = when (val result = interactor.getProduct(productId)) {
                is MarketResult.Error -> MarketProductDetailsUiState.Error(marketErrorText(result.code))
                is MarketResult.Success -> MarketProductDetailsUiState.Success(result.data)
            }
        }
    }
}
