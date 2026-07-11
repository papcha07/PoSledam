package ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.MarketInteractor
import domain.model.MarketAnimalType
import domain.model.MarketCategory
import domain.model.MarketFilter
import domain.model.MarketResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.model.MarketCatalogUiState

class MarketCatalogViewModel(
    private val interactor: MarketInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(MarketCatalogUiState())
    val state: StateFlow<MarketCatalogUiState> = _state.asStateFlow()

    private var loadJob: Job? = null
    private var currentPage = 0

    init {
        loadAllergens()
        refresh()
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun submitSearch() {
        refresh()
    }

    fun selectCategory(category: MarketCategory?) {
        _state.update { it.copy(selectedCategory = category) }
        refresh()
    }

    fun selectAnimalType(animalType: MarketAnimalType?) {
        _state.update { it.copy(selectedAnimalType = animalType) }
        refresh()
    }

    fun toggleWithoutAllergens() {
        _state.update { it.copy(withoutAllergens = !it.withoutAllergens) }
        refresh()
    }

    fun refresh() {
        loadProducts(page = 0, append = false)
    }

    fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || current.isLastPage) return
        loadProducts(page = currentPage + 1, append = true)
    }

    private fun loadAllergens() {
        viewModelScope.launch {
            when (val result = interactor.getAllergens()) {
                is MarketResult.Error -> Unit
                is MarketResult.Success -> _state.update { it.copy(allergens = result.data) }
            }
        }
    }

    private fun loadProducts(page: Int, append: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = !append,
                    isLoadingMore = append,
                    errorMessage = null
                )
            }

            val snapshot = _state.value
            val filter = MarketFilter(
                category = snapshot.selectedCategory,
                animalType = snapshot.selectedAnimalType,
                search = snapshot.searchQuery.trim(),
                excludeAllergenIds = if (snapshot.withoutAllergens) {
                    snapshot.allergens.map { it.id }
                } else {
                    emptyList()
                }
            )

            when (val result = interactor.getProducts(filter, page)) {
                is MarketResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = marketErrorText(result.code)
                        )
                    }
                }

                is MarketResult.Success -> {
                    currentPage = result.data.page
                    _state.update {
                        it.copy(
                            products = if (append) it.products + result.data.items else result.data.items,
                            isLoading = false,
                            isLoadingMore = false,
                            isLastPage = result.data.isLast,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }
}

fun marketErrorText(code: Int): String = when (code) {
    -1 -> "Нет подключения к интернету"
    401 -> "Нужно войти в аккаунт"
    404 -> "Не найдено"
    in 500..599 -> "Сервис маркета временно недоступен"
    else -> "Не удалось загрузить данные"
}
