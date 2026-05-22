package ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.SearchInteractor
import domain.models.FoundPetInfo
import domain.models.PetUiPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.InternetStatus
import ui.models.FilterDto
import ui.models.SearchState
import ui.models.TimeFilter

sealed class PetDetailsScreenState {
    data class Failed(val message: String) : PetDetailsScreenState()
    object Loading : PetDetailsScreenState()
    data class Success(val petInfo: FoundPetInfo) : PetDetailsScreenState()
    object Idle : PetDetailsScreenState()
}

class FilterViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private val _filters = MutableStateFlow(FilterDto())
    val filters: StateFlow<FilterDto> = _filters.asStateFlow()

    private val _foundState = MutableStateFlow<SearchState>(SearchState.Idle)
    val foundState: StateFlow<SearchState> = _foundState.asStateFlow()

    private val _missingState = MutableStateFlow<SearchState>(SearchState.Idle)
    val missingState: StateFlow<SearchState> = _missingState.asStateFlow()

    private val _foundResults = MutableStateFlow<List<PetUiPreview>>(emptyList())
    val foundResults: StateFlow<List<PetUiPreview>> = _foundResults.asStateFlow()

    private val _missingResults = MutableStateFlow<List<PetUiPreview>>(emptyList())
    val missingResults: StateFlow<List<PetUiPreview>> = _missingResults.asStateFlow()

    private val _isLoadingMoreFound = MutableStateFlow(false)
    val isLoadingMoreFound: StateFlow<Boolean> = _isLoadingMoreFound.asStateFlow()

    private val _isLoadingMoreMissing = MutableStateFlow(false)
    val isLoadingMoreMissing: StateFlow<Boolean> = _isLoadingMoreMissing.asStateFlow()

    private val _hasMoreFound = MutableStateFlow(true)
    val hasMoreFound: StateFlow<Boolean> = _hasMoreFound.asStateFlow()

    private val _hasMoreMissing = MutableStateFlow(true)
    val hasMoreMissing: StateFlow<Boolean> = _hasMoreMissing.asStateFlow()

    private val _petInfoState = MutableStateFlow<PetDetailsScreenState>(PetDetailsScreenState.Idle)
    val petInfoState = _petInfoState.asStateFlow()


    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

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

    fun findFoundPets() {
        viewModelScope.launch {
            _foundState.value = SearchState.Loading
            _hasMoreFound.value = true
            try {
                val filterDto = createFilterDto(lastDateTime = null)
                searchInteractor.findFoundAnnouncement(filterDto).collect { pair ->
                    val data = pair.first
                    _foundResults.value = data ?: emptyList()
                    _hasMoreFound.value = (data?.size ?: 0) >= 20
                    _foundState.value = SearchState.Success
                }
            } catch (e: Exception) {
                _foundState.value = SearchState.Error(e.message ?: "Ошибка")
            }
        }
    }

    fun loadMoreFoundPets() {
        if (!_hasMoreFound.value || _isLoadingMoreFound.value) return
        val lastDateTime = _foundResults.value.lastOrNull()?.createdAt ?: return
        viewModelScope.launch {
            _isLoadingMoreFound.value = true
            try {
                val filterDto = createFilterDto(lastDateTime = lastDateTime)
                searchInteractor.findFoundAnnouncement(filterDto).collect { pair ->
                    val data = pair.first
                    if (data != null) {
                        _foundResults.value = _foundResults.value + data
                        _hasMoreFound.value = data.size >= 20
                    }
                }
            } finally {
                _isLoadingMoreFound.value = false
            }
        }
    }

    fun findMissingPets() {
        viewModelScope.launch {
            _missingState.value = SearchState.Loading
            _hasMoreMissing.value = true
            try {
                val filterDto = createFilterDto(lastDateTime = null)
                searchInteractor.findMissingAnnouncement(filterDto).collect { pair ->
                    val data = pair.first
                    _missingResults.value = data ?: emptyList()
                    _hasMoreMissing.value = (data?.size ?: 0) >= 20
                    _missingState.value = SearchState.Success
                }
            } catch (e: Exception) {
                _missingState.value = SearchState.Error(e.message ?: "Ошибка")
            }
        }
    }

    fun loadMoreMissingPets() {
        if (!_hasMoreMissing.value || _isLoadingMoreMissing.value) return
        val lastDateTime = _missingResults.value.lastOrNull()?.createdAt ?: return
        viewModelScope.launch {
            _isLoadingMoreMissing.value = true
            try {
                val filterDto = createFilterDto(lastDateTime = lastDateTime)
                searchInteractor.findMissingAnnouncement(filterDto).collect { pair ->
                    val data = pair.first
                    if (data != null) {
                        _missingResults.value = _missingResults.value + data
                        _hasMoreMissing.value = data.size >= 20
                    }
                }
            } finally {
                _isLoadingMoreMissing.value = false
            }
        }
    }


    fun resetPetInfoState() {
        _petInfoState.value = PetDetailsScreenState.Loading
    }

    fun getInfoAboutPet(id: String) {
        viewModelScope.launch {
            _petInfoState.value = PetDetailsScreenState.Loading
            val petInfo = searchInteractor.getInfoAboutPet(id, currentTab.value)
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
