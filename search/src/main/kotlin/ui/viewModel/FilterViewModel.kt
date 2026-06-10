package ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import domain.interactor.SearchInteractor
import domain.models.FilterDto
import domain.models.FoundPetInfo
import domain.models.PetUiPreview
import domain.user.UserInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.InternetStatus
import ui.models.TimeFilter

sealed class PetDetailsScreenState {
    data class Failed(val message: String) : PetDetailsScreenState()
    object Loading : PetDetailsScreenState()
    data class Success(val petInfo: FoundPetInfo) : PetDetailsScreenState()
    object Idle : PetDetailsScreenState()
}

class FilterViewModel(
    private val searchInteractor: SearchInteractor,
    private val userInteractor: UserInteractor
) : ViewModel() {

    private val _filters = MutableStateFlow(FilterDto(searchRadius = 5))
    val filters: StateFlow<FilterDto> = _filters.asStateFlow()

    private val _petInfoState = MutableStateFlow<PetDetailsScreenState>(PetDetailsScreenState.Idle)
    val petInfoState = _petInfoState.asStateFlow()

    val userState = userInteractor.observeUser()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val findPets: Flow<PagingData<PetUiPreview>> =
        combine(
            filters,
            userInteractor.observeLocation()
                .map { location ->
                    location?.latitude to location?.longitude
                }
                .distinctUntilChanged()
        ) { filter, location ->
            val latitude = location.first
            val longitude = location.second

            filter.copy(
                lastDateTime = null,
                searchCenterLatitude = latitude,
                searchCenterLongitude = longitude
            )
        }
            .distinctUntilChanged()
            .flatMapLatest { filter ->
                searchInteractor.loadFindAnnouncementPage(filter)
            }
            .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val missingPets: Flow<PagingData<PetUiPreview>> =
        combine(
            filters,
            userInteractor.observeLocation()
                .map { location ->
                    location?.latitude to location?.longitude
                }
                .distinctUntilChanged()
        ) { filter, location ->
            val latitude = location.first
            val longitude = location.second

            filter.copy(
                lastDateTime = null,
                searchCenterLatitude = latitude,
                searchCenterLongitude = longitude
            )
        }
            .distinctUntilChanged()
            .flatMapLatest { filter ->
                searchInteractor.loadMissAnnouncementPage(filter)
            }
            .cachedIn(viewModelScope)


    fun setCurrentTab(tabIndex: Int) {
        Log.d("announcementType", "viewModel ${tabIndex}")
        _currentTab.value = tabIndex
    }
    //0 - найденные , 1 - пропажи

    fun setDistrict(value: String?) {
        _filters.update { it.copy(district = value) }
    }

    fun setTime(value: TimeFilter?) {
        _filters.update { it.copy(time = value) }
    }

    fun setType(value: Int?) {
        _filters.update { it.copy(typeOfPet = value) }
    }

    fun setGender(value: Int?) {
        _filters.update { it.copy(gender = value) }
    }

    fun setRadius(radius: Int) {
        _filters.update {
            it.copy(searchRadius = radius)
        }
    }


    val chips: StateFlow<List<FilterChipUi>> =
        filters.map { it.toChips() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun clearChip(key: String) {
        when (key) {
            "district" -> setDistrict(null)
            "time" -> setTime(null)
            "type" -> setType(null)
            "gender" -> setGender(null)
        }
    }

    private fun createFilterDto(lastDateTime: String? = null): FilterDto {
        return FilterDto(
            district = _filters.value.district,
            time = _filters.value.time,
            typeOfPet = _filters.value.typeOfPet,
            gender = _filters.value.gender,
            lastDateTime = lastDateTime
        )
    }

    fun resetPetInfoState() {
        _petInfoState.value = PetDetailsScreenState.Loading
    }

    fun getInfoAboutPet(
        id: String,
        announcementType: Int = currentTab.value
    ) {
        viewModelScope.launch {
            _petInfoState.value = PetDetailsScreenState.Loading

            val petInfo = searchInteractor.getInfoAboutPet(
                id = id,
                announcementType = announcementType
            )

            val data = petInfo.first
            val message = petInfo.second

            if (data != null) {
                _petInfoState.update {
                    PetDetailsScreenState.Success(data)
                }
            }

            if (message != null) {
                when (message) {
                    InternetStatus.NoInternet -> {
                        _petInfoState.update {
                            PetDetailsScreenState.Failed("Проблемы с интернетом")
                        }
                    }

                    InternetStatus.Error -> {
                        _petInfoState.update {
                            PetDetailsScreenState.Failed("Что-то пошло не так..")
                        }
                    }
                }
            }
        }
    }
}

data class FilterChipUi(
    val key: String,
    val text: String
)

private fun FilterDto.toChips(): List<FilterChipUi> = buildList {
    district?.let { add(FilterChipUi("district", it)) }
    time?.let {
        val text = when (it) {
            TimeFilter.TODAY -> "Сегодня"
            TimeFilter.WEEK -> "На неделе"
            TimeFilter.MONTH -> "Месяц"
            TimeFilter.YESTERDAY -> "Вчера"
            TimeFilter.THREE_DAYS -> "3 дня"
        }
        add(FilterChipUi("time", text))
    }
    typeOfPet?.let {
        val text = when (it) {
            0 -> "Коты"; 1 -> "Собаки"; else -> "Другое"
        }
        add(FilterChipUi("type", text))
    }
    gender?.let {
        val text = when (it) {
            1 -> "Самка"; 0 -> "Самец"; else -> "Пол: $it"
        }
        add(FilterChipUi("gender", text))
    }
}
