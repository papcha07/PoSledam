package ui.screen.mainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.location.LocationInteractor
import domain.interactor.location.LocationSendResult
import domain.interactor.street.StreetPetInteractor
import domain.model.StreetAnimalParams
import domain.models.StreetPetPreviewModel
import domain.notification.Notification
import domain.notification.NotificationInteractor
import domain.user.UserInteractor
import helper.LocationSyncRequestStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import worker.location_worker.WorkerInteractor

class MainScreenViewModel(
    private val notificationInteractor: NotificationInteractor,
    private val workerInteractor: WorkerInteractor,
    private val locationInteractor: LocationInteractor,
    private val locationSyncRequestStore: LocationSyncRequestStore,
    private val streetPetInteractor: StreetPetInteractor,
    private val userInteractor: UserInteractor,
) : ViewModel() {
    private val _locationSendState =
        MutableStateFlow<LocationSendUiState>(LocationSendUiState.Idle)
    val locationSendState = _locationSendState.asStateFlow()

    private val _latestStreetPetState =
        MutableStateFlow<LatestStreetPetState>(LatestStreetPetState.Loading)
    val latestStreetPetState = _latestStreetPetState.asStateFlow()

    private var locationSendJob: Job? = null
    private var locationWorkerStarted = false

    val notificationState: StateFlow<List<Notification>> =
        notificationInteractor
            .getAllNotificationEntity()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        observeLatestStreetPet()
    }

    private fun observeLatestStreetPet() {
        viewModelScope.launch {
            userInteractor
                .observeLocation()
                .distinctUntilChanged()
                .collectLatest { location ->
                    if (location == null) {
                        _latestStreetPetState.value = LatestStreetPetState.Placeholder
                        return@collectLatest
                    }

                    _latestStreetPetState.value = LatestStreetPetState.Loading

                    val (streetPet, error) = streetPetInteractor.getLatestStreetAnimal(
                        StreetAnimalParams(
                            centerRadius = DEFAULT_STREET_RADIUS,
                            searchCenterLatitude = location.latitude,
                            searchCenterLongitude = location.longitude
                        )
                    )

                    _latestStreetPetState.value = if (streetPet != null && error == null) {
                        LatestStreetPetState.Content(streetPet)
                    } else {
                        LatestStreetPetState.Placeholder
                    }
                }
        }
    }

    fun markAllNotifications() {
        viewModelScope.launch {
            notificationInteractor.allMark()
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            notificationInteractor.deleteById(id)
        }
    }

    private fun startLocationWorker() {
        if (locationWorkerStarted) return
        locationWorkerStarted = true
        workerInteractor.startLocationWorker()
    }

    fun onForegroundLocationPermissionGranted() {
        if (locationSendJob?.isActive == true) return
        if (_locationSendState.value == LocationSendUiState.Success) return

        locationSendJob = viewModelScope.launch {
            _locationSendState.value = LocationSendUiState.PermissionGranted
            if (locationSyncRequestStore.isSendAfterLoginPending()) {
                locationSyncRequestStore.clearSendAfterLoginPending()
            }

            val result = locationInteractor.sendCurrentLocation()
            _locationSendState.value = result.toUiState()

            when (result) {
                LocationSendResult.Success -> Log.d("USER_LOCATION", "Current location sent")
                LocationSendResult.PermissionDenied -> Log.d(
                    "USER_LOCATION",
                    "Location permission denied"
                )

                LocationSendResult.LocationUnavailable -> Log.d(
                    "USER_LOCATION",
                    "Current location unavailable"
                )

                is LocationSendResult.NetworkError -> Log.d("USER_LOCATION", "Location send failed")
            }
        }
    }

    fun onForegroundLocationPermissionDenied() {
        _locationSendState.value = LocationSendUiState.PermissionDenied
    }

    fun onForegroundLocationPermissionPermanentlyDenied() {
        _locationSendState.value = LocationSendUiState.PermissionPermanentlyDenied
    }

    fun onBackgroundLocationPermissionGranted() {
        startLocationWorker()
    }

    fun onBackgroundLocationPermissionDenied() {
        Log.d("WORKER_MANAGER", "Background location permission is not granted")
    }
}

sealed interface LatestStreetPetState {
    data object Loading : LatestStreetPetState
    data object Placeholder : LatestStreetPetState
    data class Content(val streetPet: StreetPetPreviewModel) : LatestStreetPetState
}

sealed interface LocationSendUiState {
    data object Idle : LocationSendUiState
    data object PermissionGranted : LocationSendUiState
    data object PermissionDenied : LocationSendUiState
    data object PermissionPermanentlyDenied : LocationSendUiState
    data object LocationUnavailable : LocationSendUiState
    data object NetworkError : LocationSendUiState
    data object Success : LocationSendUiState
}

private fun LocationSendResult.toUiState(): LocationSendUiState {
    return when (this) {
        LocationSendResult.Success -> LocationSendUiState.Success
        LocationSendResult.PermissionDenied -> LocationSendUiState.PermissionDenied
        LocationSendResult.LocationUnavailable -> LocationSendUiState.LocationUnavailable
        is LocationSendResult.NetworkError -> LocationSendUiState.NetworkError
    }
}

private const val DEFAULT_STREET_RADIUS = 40
