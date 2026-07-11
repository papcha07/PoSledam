package ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.AiSearchInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.InternetStatus
import ui.models.AiSearchResultUiState

class AiSearchResultViewModel(
    private val interactor: AiSearchInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<AiSearchResultUiState>(AiSearchResultUiState.Loading)
    val state: StateFlow<AiSearchResultUiState> = _state.asStateFlow()

    private var loadedRequestId: String? = null

    fun load(requestId: String, force: Boolean = false) {
        if (!force && requestId == loadedRequestId &&
            _state.value is AiSearchResultUiState.Success
        ) return

        loadedRequestId = requestId
        viewModelScope.launch {
            _state.value = AiSearchResultUiState.Loading

            val (result, status) = interactor.getSearchResult(requestId)

            _state.value = when {
                result != null -> AiSearchResultUiState.Success(result)
                status == InternetStatus.NoInternet ->
                    AiSearchResultUiState.Error("Проблемы с соединением")

                else -> AiSearchResultUiState.Error("Не удалось загрузить результат поиска")
            }
        }
    }
}
