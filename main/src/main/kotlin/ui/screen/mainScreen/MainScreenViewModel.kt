package ui.screen.mainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.location.LocationInteractor
import domain.interactor.location.LocationSendResult
import domain.notification.Notification
import domain.notification.NotificationInteractor
import domain.user.UserInteractor
import helper.LocationSyncRequestStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import worker.location_worker.WorkerInteractor

class MainScreenViewModel(
    private val notificationInteractor: NotificationInteractor,
    private val userInteractor: UserInteractor,
    private val workerInteractor: WorkerInteractor,
    private val locationInteractor: LocationInteractor,
    private val locationSyncRequestStore: LocationSyncRequestStore
) : ViewModel() {
    private val _locationSendState =
        MutableStateFlow<LocationSendUiState>(LocationSendUiState.Idle)
    val locationSendState = _locationSendState.asStateFlow()

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
                LocationSendResult.PermissionDenied -> Log.d("USER_LOCATION", "Location permission denied")
                LocationSendResult.LocationUnavailable -> Log.d("USER_LOCATION", "Current location unavailable")
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

    fun refreshUser() {
        viewModelScope.launch {
            userInteractor.refreshUser()
        }
    }
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
