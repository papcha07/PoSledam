package ui.screen.mainScreen

import ApiResponse
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.NotificationInteractor
import domain.interactor.LocationInteractor
import domain.interactor.MainInteractor
import domain.model.Notification
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.components.profilebar.ProfileBarState
import ui.model.UserDataUiInfo

sealed class MainScreenState {
    data object Idle : MainScreenState()
    data object Loading : MainScreenState()
    data class Success(val userInfo: UserDataUiInfo) : MainScreenState()
    data class Failed(val message: String) : MainScreenState()
}

class MainScreenViewModel(
    private val mainInteractor: MainInteractor,
    private val notificationInteractor: NotificationInteractor,
    private val locationInteractor: LocationInteractor
) : ViewModel() {
    private val _userInfoState = MutableStateFlow<ProfileBarState>(ProfileBarState.Idle)
    val userInfoState = _userInfoState.asStateFlow()

    val notificationState: StateFlow<List<Notification>> =
        notificationInteractor
            .getAllNotificationEntity()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val markNotificationState: StateFlow<Boolean> =
        notificationState
            .map { it.any { notification -> !notification.isRead } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                false
            )


    fun setAllMark() {
        viewModelScope.launch {
            notificationInteractor.allMark()
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            notificationInteractor.deleteById(id)
        }
    }

    fun sendCurrentLocation() {
        viewModelScope.launch {
            try {
                locationInteractor.sendCurrentLocation()
            } catch (e: Exception) {
                Log.e("MainScreenViewModel", "Failed to send current location", e)
            }
        }
    }

    fun testViewModelScope(){
        val vmScope = viewModelScope
        vmScope.launch {
            val job1 = launch {

            }

            val job2 = launch {

            }

        }
        vmScope.cancel()
    }

    fun loadUser() {
        _userInfoState.update {
            ProfileBarState.Loading
        }
        viewModelScope.launch {
            val cachedUser = mainInteractor.getUserFromCache()
            if (cachedUser != null) {
                _userInfoState.update {
                    ProfileBarState.Success(cachedUser)
                }
            } else {
                when (val result = mainInteractor.syncUserFromServer()) {
                    is ApiResponse.Error -> {
                        _userInfoState.update {
                            ProfileBarState.Failed("Не удалось получить информацию о пользователе")
                        }
                    }

                    is ApiResponse.Success<UserDataUiInfo> -> {
                        _userInfoState.update {
                            ProfileBarState.Success(result.data)
                        }
                    }
                }
            }
        }
    }

}