package ui.street

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.StreetPetInteractor
import domain.models.StreetPetPreviewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class StreetPetScreenState {
    data class Success(val data: List<StreetPetPreviewModel>) : StreetPetScreenState()
    data object Failed : StreetPetScreenState()
    data object Empty : StreetPetScreenState()
    data object Idle : StreetPetScreenState()
}

class StreetPetViewModel(
    private val streetPetInteractor: StreetPetInteractor
) : ViewModel() {

    private val _animalScreenState =
        MutableStateFlow<StreetPetScreenState>(StreetPetScreenState.Idle)
    val animalScreenState = _animalScreenState.asStateFlow()

    fun getStreetAnimals() {
        viewModelScope.launch {
            streetPetInteractor.getStreetAnimals().collect { pair ->
                val animals = pair.first

                _animalScreenState.value = when {
                    animals == null -> StreetPetScreenState.Failed
                    animals.isEmpty() -> StreetPetScreenState.Empty
                    else -> StreetPetScreenState.Success(animals)
                }
            }
            Log.d("animalScreenState", animalScreenState.value.toString())
        }
    }





}