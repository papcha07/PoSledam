package ui.screen.street

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import domain.interactor.street.StreetPetInteractor
import domain.model.StreetAnimalParams
import domain.models.StreetDetails
import domain.models.StreetPetPreviewModel
import domain.user.UserInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ui.model.ScreenState


class StreetPetViewModel(
    private val streetPetInteractor: StreetPetInteractor,
    private val userInteractor: UserInteractor
) : ViewModel() {

    private val _detailsState = MutableStateFlow<ScreenState<StreetDetails>>(ScreenState.Idle)
    val detailsState = _detailsState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val streetAnimals: Flow<PagingData<StreetPetPreviewModel>> =
        userInteractor
            .observeLocation()
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { location ->
                streetPetInteractor.getStreetAnimals(
                    StreetAnimalParams(
                        centerRadius = DEFAULT_RADIUS,
                        searchCenterLatitude = location.latitude,
                        searchCenterLongitude = location.longitude
                    )
                )
            }
            .cachedIn(viewModelScope)

    fun getDetailsAboutAnimal(id: String) {
        viewModelScope.launch {
            _detailsState.value = ScreenState.Loading
            val request = streetPetInteractor.getInfoAboutStreetAnimal(id)
            val animal = request.first
            if (animal != null) {
                _detailsState.value = ScreenState.Success(animal)
                return@launch
            }
            when (request.second) {
                400 -> _detailsState.value = ScreenState.Error
                -1 -> _detailsState.value = ScreenState.InternetError
            }
        }
    }


    companion object {
        private const val DEFAULT_RADIUS = 40
    }
}