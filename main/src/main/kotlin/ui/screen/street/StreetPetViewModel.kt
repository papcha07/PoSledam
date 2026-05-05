package ui.screen.street

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.StreetPetInteractor
import domain.models.StreetPetPreviewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class StreetPetScreenState {
    data class Success(val data: List<StreetPetPreviewModel>) : StreetPetScreenState()
    data object Failed : StreetPetScreenState()
    data object Empty : StreetPetScreenState()
    data object Idle : StreetPetScreenState()
    data object Loading : StreetPetScreenState()
}

class StreetPetViewModel(
    private val streetPetInteractor: StreetPetInteractor
) : ViewModel() {
    private val _animalScreenState =
        MutableStateFlow<StreetPetScreenState>(StreetPetScreenState.Idle)
    val animalScreenState = _animalScreenState.asStateFlow()


    fun getStreetAnimals() {
        viewModelScope.launch {
            _animalScreenState.value = StreetPetScreenState.Loading
            delay(200)
            val streetResult = streetPetInteractor.getStreetAnimals()
            val animalList = streetResult.first
            _animalScreenState.value = when {
                animalList == null -> StreetPetScreenState.Failed
                animalList.isEmpty() -> StreetPetScreenState.Empty
                else -> StreetPetScreenState.Success(animalList)
            }
        }
    }

}