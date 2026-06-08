package com.example.alinaposledam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.notification.NotificationInteractor
import domain.user.UserInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ui.components.profilebar.ProfileBarState

class ProfileBarViewModel(
    private val userInteractor: UserInteractor,
    notificationInteractor: NotificationInteractor
) : ViewModel() {

    private val _profileBarState = MutableStateFlow<ProfileBarState>(ProfileBarState.Idle)
    val profileBarState = _profileBarState.asStateFlow()

    val notificationsIsNotRead: StateFlow<Boolean> =
        notificationInteractor
            .getAllNotificationEntity()
            .map { notifications -> notifications.any { notification -> !notification.isRead } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    private var observeUserJob: Job? = null

    init {
        observeUser()
    }

    private fun observeUser() {
        if (observeUserJob?.isActive == true) return

        observeUserJob = viewModelScope.launch {
            _profileBarState.value = ProfileBarState.Loading
            userInteractor.observeUser().collect { user ->
                _profileBarState.value = if (user == null) {
                    ProfileBarState.Loading
                } else {
                    ProfileBarState.Success(user)
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
