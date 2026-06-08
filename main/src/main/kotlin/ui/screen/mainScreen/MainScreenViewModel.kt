package ui.screen.mainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.notification.Notification
import domain.notification.NotificationInteractor
import domain.user.UserInteractor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ui.LocationProvider
import worker.location_worker.WorkerInteractor

class MainScreenViewModel(
    private val notificationInteractor: NotificationInteractor,
    private val userInteractor: UserInteractor,
    private val workerInteractor: WorkerInteractor,
    private val locationProvider: LocationProvider
) : ViewModel() {

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

    fun startLocationWorker() {
        workerInteractor.startLocationWorker()
    }

    suspend fun updateUserLocation(): Boolean {
        val location = locationProvider.getCurrentLocation()

        if (location == null) {
            Log.d("USER_LOCATION", "LocationDto is null")
            return false
        }

        userInteractor.updateUserLocation(
            location.latitude,
            location.longitude
        )

        Log.d("USER_LOCATION", "LocationDto Updated: ${location.latitude}, ${location.longitude}")

        return true
    }

    fun refreshUser() {
        viewModelScope.launch {
            userInteractor.refreshUser()
        }
    }
}
