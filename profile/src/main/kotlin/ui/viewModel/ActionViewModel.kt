package ui.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.announcement.AnnouncementInteractor
import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.model.Location
import domain.notification.NotificationSettingsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.geo.AddressSuggestion
import ui.LocationProvider
import ui.model.ActionScreenState
import yandex_core.NetworkResource
import yandex_core.YandexInteractor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


data class ActionScreenData(
    var name: String? = null,
    var breed: String = "",
    var color: String = "",
    var description: String = "",
    var typeOfPet: Int? = null,
    var gender: Int? = null,
    val selectedImageUris: List<Uri> = emptyList(),
    var selectedDate: LocalDate? = null,
    var selectedTime: LocalTime? = null,
    val mapCameraLocation: Location? = null,
    var lat: Double? = null,
    var lon: Double? = null
) {
    private val formatter =
        DateTimeFormatter.ofPattern("dd/MM HH:mm")
    val isActionComponentValid: Boolean
        get() = !name.isNullOrBlank()
                && typeOfPet != null
                && breed.isNotBlank()
                && color.isNotBlank()
                && gender != null
                && description.isNotBlank()

    val isAddressComponentValid: Boolean
        get() = selectedTime != null
                && selectedDate != null
                && lat != null
                && lon != null

    val isFormValid: Boolean
        get() = isActionComponentValid && isAddressComponentValid


    val formattedDateTime: String
        get() = if (selectedTime != null && selectedDate != null) {
            LocalDateTime.of(selectedDate, selectedTime)
                .format(formatter)
        } else {
            "Дата не выбрана"
        }


}

enum class ActionPage { MAIN, ADDRESS, RESULT }

class ActionViewModel(
    private val announcementInteractor: AnnouncementInteractor,
    private val yandexInteractor: YandexInteractor,
    private val notificationSettingsInteractor: NotificationSettingsInteractor,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val _pageState = MutableStateFlow(ActionPage.MAIN)
    val pageState: StateFlow<ActionPage> = _pageState.asStateFlow()

    private val _methodValueFlow = MutableStateFlow<Int>(0)
    val methodValueFlow: StateFlow<Int> = _methodValueFlow.asStateFlow()

    fun clearPageState() {
        _pageState.value = ActionPage.MAIN
    }

    fun updateMethodValue(method: Int) {
        Log.d("METHOD", method.toString())
        _methodValueFlow.value = method
    }

    fun goToAddressPage() {
        _pageState.value = ActionPage.ADDRESS
    }

    fun setCurrentLocation() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                _state.update {
                    it.copy(
                        mapCameraLocation = Location(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                }
            }
        }
    }


    fun goToResultPage() {
        _pageState.value = ActionPage.RESULT
    }

    fun goToMainPage() {
        _pageState.value = ActionPage.MAIN
    }

    fun updateScreenState() {
        _pageState.update { if (it == ActionPage.MAIN) ActionPage.ADDRESS else ActionPage.MAIN }
    }

    private var _state = MutableStateFlow(ActionScreenData())
    val state: StateFlow<ActionScreenData> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ActionScreenData()
    )

    fun clearState() {
        _pageState.value = ActionPage.MAIN
        _state.value = ActionScreenData()
    }

    val isMainActionComponentState =
        state.map {
            it.isActionComponentValid
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val isAddressComponentState =
        state.map {
            it.isAddressComponentValid
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val isFormValidState = state.map {
        it.isFormValid
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    private var _uiState = MutableStateFlow<ActionScreenState>(ActionScreenState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    var addressText by mutableStateOf("")

    init {
        viewModelScope.launch {
            _notificationsEnabled.value = notificationSettingsInteractor.isNotificationsEnabled()
        }
    }

    fun updateName(value: String) {
        _state.update {
            it.copy(name = value)
        }
    }

    fun dismissSuccess() {
        _uiState.update { ActionScreenState.Idle }
    }

    fun enableNotifications() {
        viewModelScope.launch {
            notificationSettingsInteractor.setNotificationsEnabled(true)
            _notificationsEnabled.value = true
        }
    }

    fun updateBreed(value: String) {
        _state.update {
            it.copy(breed = value)
        }
    }

    fun updateColor(value: String) {
        _state.update {
            it.copy(color = value)
        }
    }

    fun updateDescription(value: String) {
        _state.update {
            it.copy(description = value)
        }
    }

    fun updateTypeOfPet(value: Int) {
        _state.update {
            it.copy(typeOfPet = value)
        }
    }

    fun updateGender(value: Int) {
        _state.update {
            it.copy(gender = value)
        }
    }

    fun addImage(uri: Uri) {
        _state.update { state ->
            state.copy(
                selectedImageUris = state.selectedImageUris + uri
            )
        }
    }

    fun removeImage(uri: Uri) {
        _state.update { state ->
            state.copy(
                selectedImageUris = state.selectedImageUris.filterNot { it == uri }
            )
        }
    }


    fun updateSelectedDate(date: LocalDate) {
        Log.i("updateSelectedDate", "date = $date")
        _state.update {
            it.copy(selectedDate = date)
        }
    }

    fun updateSelectedTime(time: LocalTime) {
        Log.i("updateSelectedTime", "time = $time")
        _state.update {
            it.copy(selectedTime = time)
        }
    }

    fun updateLongitude(long: Double) {
        _state.update {
            it.copy(lon = long)
        }
    }

    fun updateLatitude(latitude: Double) {
        _state.update {
            it.copy(lat = latitude)
        }
    }

    fun createAnnouncement() {
        viewModelScope.launch {
            _uiState.value = ActionScreenState.Loading

            val s = _state.value
            val status = announcementInteractor.sendAnnouncement(
                announcementInfo = AnnouncementInfo(
                    location = Location(s.lat!!, s.lon!!),
                    petType = s.typeOfPet!!,
                    gender = s.gender!!,
                    color = s.color,
                    breed = s.breed,
                    petName = s.name!!,
                    eventDate = s.selectedDate!!,
                    time = s.selectedTime!!,
                    description = s.description
                ),
                files = s.selectedImageUris.map { it.toString() },
                type = _methodValueFlow.value
            )
            Log.d("pets", status.toString())

            _uiState.value = when (status) {
                is AnnouncementStatus.Success -> ActionScreenState.SuccessAction
                is AnnouncementStatus.Failed -> ActionScreenState.FailedAction("Что-то пошло не так")
            }
        }
    }


    fun getAddressList(lon: Double, lat: Double) {
        viewModelScope.launch {
            val result = yandexInteractor.resolvePointOnceOne(lon = lon, lat = lat)
            when (result) {
                is NetworkResource.Failed<*> -> {
                    Log.d("resolvePoint", "Error")
                }

                is NetworkResource.Success<AddressSuggestion> -> {
                    Log.d("resolvePoint", result.data.address)
                    addressText = result.data.address
                }
            }
        }
    }

    fun clearAddressRow() {
        addressText = ""
    }

}
