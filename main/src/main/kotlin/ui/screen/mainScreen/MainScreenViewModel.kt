package ui.screen.mainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import domain.interactor.SearchInteractor
import domain.interactor.location.LocationInteractor
import domain.interactor.location.LocationSendResult
import domain.interactor.street.StreetPetInteractor
import domain.model.StreetAnimalParams
import domain.models.FilterDto
import domain.models.PetUiPreview
import domain.models.StreetPetPreviewModel
import domain.notification.Notification
import domain.notification.NotificationInteractor
import domain.user.UserInteractor
import helper.LocationSyncRequestStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
// import worker.location_worker.WorkerInteractor

class MainScreenViewModel(
    private val notificationInteractor: NotificationInteractor,
    // Background location worker is temporarily disabled for moderation.
    // private val workerInteractor: WorkerInteractor,
    private val locationInteractor: LocationInteractor,
    private val locationSyncRequestStore: LocationSyncRequestStore,
    private val streetPetInteractor: StreetPetInteractor,
    private val userInteractor: UserInteractor,
    private val searchInteractor: SearchInteractor,
) : ViewModel() {
    private val _locationSendState =
        MutableStateFlow<LocationSendUiState>(LocationSendUiState.Idle)
    val locationSendState = _locationSendState.asStateFlow()

    private val _latestStreetPetState =
        MutableStateFlow<LatestStreetPetState>(LatestStreetPetState.Loading)
    val latestStreetPetState = _latestStreetPetState.asStateFlow()

    private var locationSendJob: Job? = null
    // Background location worker is temporarily disabled for moderation.
    // private var locationWorkerStarted = false
    private var permissionFlowStarted = false

    val notificationState: StateFlow<List<Notification>> =
        notificationInteractor
            .getAllNotificationEntity()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayMissingPets: Flow<PagingData<PetUiPreview>> =
        userInteractor.observeLocation()
            .map { location ->
                location?.latitude to location?.longitude
            }
            .distinctUntilChanged()
            .flatMapLatest { location ->
                val latitude = location.first
                val longitude = location.second
                val hasSearchCenter = latitude != null && longitude != null

                searchInteractor.loadMissAnnouncementPage(
                    FilterDto(
                        lastDateTime = null,
                        searchRadius = DEFAULT_SEARCH_RADIUS.takeIf { hasSearchCenter },
                        searchCenterLatitude = latitude,
                        searchCenterLongitude = longitude
                    )
                )
            }
            .map { pagingData ->
                pagingData.filter { pet ->
                    pet.createdAt.isToday()
                }
            }
            .cachedIn(viewModelScope)

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

    // Background location worker is temporarily disabled for moderation.
    // private fun startLocationWorker() {
    //     if (locationWorkerStarted) return
    //     locationWorkerStarted = true
    //     workerInteractor.startLocationWorker()
    // }

    fun consumePermissionFlowLaunch(): Boolean {
        if (permissionFlowStarted) return false
        permissionFlowStarted = true
        return true
    }

    fun onForegroundLocationPermissionGranted() {
        if (locationSendJob?.isActive == true) return

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
        Log.d("USER_LOCATION", "Foreground location permission denied")
        _locationSendState.value = LocationSendUiState.PermissionDenied
    }

    fun onForegroundLocationPermissionPermanentlyDenied() {
        Log.d("USER_LOCATION", "Foreground location permission permanently denied")
        _locationSendState.value = LocationSendUiState.PermissionPermanentlyDenied
    }

    // Background location worker is temporarily disabled for moderation.
    // fun onBackgroundLocationPermissionGranted() {
    //     Log.d("WORKER_MANAGER", "Background location permission granted")
    //     startLocationWorker()
    // }
    //
    // fun onBackgroundLocationPermissionDenied() {
    //     Log.d("WORKER_MANAGER", "Background location permission is not granted")
    // }
}

private fun String?.isToday(): Boolean {
    val createdAt = this ?: return false
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)

    val createdDate = runCatching {
        OffsetDateTime.parse(createdAt)
            .atZoneSameInstant(zone)
            .toLocalDate()
    }.getOrElse {
        runCatching {
            Instant.parse(createdAt)
                .atZone(zone)
                .toLocalDate()
        }.getOrNull()
    }

    return createdDate == today
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
private const val DEFAULT_SEARCH_RADIUS = 5
