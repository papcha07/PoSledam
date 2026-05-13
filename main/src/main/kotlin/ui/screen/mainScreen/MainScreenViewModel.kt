package ui.screen.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.notification.Notification
import domain.notification.NotificationInteractor
import domain.user.UserInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val notificationInteractor: NotificationInteractor,
    private val userInteractor: UserInteractor
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

    fun deleteById(id: Long) {
        viewModelScope.launch {
            notificationInteractor.deleteById(id)
        }
    }

    fun observeUser() {
        viewModelScope.launch {
            _userInfoState.value = ProfileBarState.Loading
            userInteractor.observeUser().collect { user ->
                if (user == null) {
                    _userInfoState.value = ProfileBarState.Loading
                } else {
                    _userInfoState.value = ProfileBarState.Success(user)
                }
            }
        }
    }

    fun refreshUser() {
        viewModelScope.launch {
            userInteractor.refreshUser()
        }
    }
}