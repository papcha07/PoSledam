package ui.components.profilebar

import domain.user.model.User

sealed class ProfileBarState {
    data object Idle : ProfileBarState()
    data class Success(val userDataInfo: User) : ProfileBarState()
    data class Failed(val message: String) : ProfileBarState()
    data object Loading : ProfileBarState()
}

sealed class ProfileBarCityState {
    data object Idle : ProfileBarCityState()
    data object Loading : ProfileBarCityState()
    data class Success(val city: String) : ProfileBarCityState()
    data class Failed(val message: String) : ProfileBarCityState()
}
