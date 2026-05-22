package ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.SearchInteractor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.model.Response

data class ReportFoundAnimalUiState(
    val isSuccess: Boolean = false
)

sealed interface ReportFoundAnimalEffect {
    data object InternetError : ReportFoundAnimalEffect
    data object ServerError : ReportFoundAnimalEffect
}

class ReportViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportFoundAnimalUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ReportFoundAnimalEffect>()
    val effect = _effect.asSharedFlow()

    fun reportFoundAnimal(id: String) {
        viewModelScope.launch {
            val response = searchInteractor.reportFoundAnimal(id)
            when (response) {
                Response.SUCCESS -> {
                    _uiState.update { it.copy(isSuccess = true) }
                }

                Response.INTERNET_ERROR -> {
                    _effect.emit(ReportFoundAnimalEffect.InternetError)
                }

                Response.SERVER_ERROR -> {
                    _effect.emit(ReportFoundAnimalEffect.ServerError)
                }
            }
        }
    }


}
