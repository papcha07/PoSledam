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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import model.geo.AddressSuggestion
import ui.components.profilebar.ProfileBarCityState
import ui.components.profilebar.ProfileBarState
import yandex_core.NetworkResource
import yandex_core.YandexInteractor

class ProfileBarViewModel(
    private val userInteractor: UserInteractor,
    private val yandexInteractor: YandexInteractor,
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

    private val _cityState = MutableStateFlow<ProfileBarCityState>(ProfileBarCityState.Idle)
    val cityState = _cityState.asStateFlow()
    private var observeUserJob: Job? = null
    private var observeLocationJob: Job? = null

    init {
        observeUser()
        observeLocation()
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

    private fun observeLocation() {
        if (observeLocationJob?.isActive == true) return

        observeLocationJob = viewModelScope.launch {
            userInteractor.observeLocation()
                .distinctUntilChanged()
                .collectLatest { location ->
                    if (location == null) {
                        _cityState.value = ProfileBarCityState.Loading
                        return@collectLatest
                    }

                    _cityState.value = ProfileBarCityState.Loading
                    val result = yandexInteractor.resolvePointOnceOne(
                        lon = location.longitude,
                        lat = location.latitude
                    )

                    _cityState.value = when (result) {
                        is NetworkResource.Failed<*> -> {
                            ProfileBarCityState.Failed("Город не определён")
                        }

                        is NetworkResource.Success<AddressSuggestion> -> {
                            result.data.city
                                ?.takeIf { city -> city.isNotBlank() }
                                ?.let { city -> ProfileBarCityState.Success(city) }
                                ?: ProfileBarCityState.Failed("Город не определён")
                        }
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
