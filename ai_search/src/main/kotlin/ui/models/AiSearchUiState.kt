package ui.models

import domain.models.AiSearchResult

/**
 * Состояние экрана результата конкретного запроса (GET /api/search/{id}),
 * отображается в чат-формате.
 */
sealed interface AiSearchResultUiState {
    data object Loading : AiSearchResultUiState
    data class Success(val result: AiSearchResult) : AiSearchResultUiState
    data class Error(val message: String) : AiSearchResultUiState
}
