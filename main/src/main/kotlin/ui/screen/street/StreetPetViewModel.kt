package ui.screen.street

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import domain.interactor.street.StreetPetInteractor
import domain.models.StreetPetPreviewModel
import kotlinx.coroutines.flow.Flow

class StreetPetViewModel(
    private val streetPetInteractor: StreetPetInteractor
) : ViewModel() {

    val streetAnimals: Flow<PagingData<StreetPetPreviewModel>> =
        streetPetInteractor.getStreetAnimals()
            .cachedIn(viewModelScope)
}