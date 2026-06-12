package ui.viewModel

import android.net.Uri
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
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface ReportFoundAnimalEffect {
    data object InternetError : ReportFoundAnimalEffect
    data object ServerError : ReportFoundAnimalEffect
}

data class SpottedAnimalData(
    val lon: Double? = null,
    val lat: Double? = null,
    val uri: List<Uri> = emptyList()
)

class ReportViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportFoundAnimalUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ReportFoundAnimalEffect>()
    val effect = _effect.asSharedFlow()

    private val _spottedUiState = MutableStateFlow<SpottedAnimalData>(SpottedAnimalData())
    val spottedAnimalData = _spottedUiState.asStateFlow()


    private val _findUriState = MutableStateFlow<List<Uri>>(listOf())
    val findUriState = _findUriState.asStateFlow()

    fun reportFoundAnimal(id: String) {
        viewModelScope.launch {
            val response = searchInteractor.reportFoundAnimal(id, _findUriState.value)
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

    fun reportSpottedAnimal(id: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val spottedData = _spottedUiState.value
            val response = searchInteractor.reportSpottedAnimal(id, spottedData)
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
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun updateLatitude(lat: Double) {
        _spottedUiState.update {
            it.copy(lat = lat)
        }
    }

    fun updateLongitude(lon: Double) {
        _spottedUiState.update {
            it.copy(lon = lon)
        }
    }

    fun addImage(uri: Uri) {
        _spottedUiState.update { state ->
            state.copy(
                uri = state.uri + uri
            )
        }
    }

    fun addFindImage(uri: Uri) {
        _findUriState.update { state ->
            state + uri
        }
    }
}
