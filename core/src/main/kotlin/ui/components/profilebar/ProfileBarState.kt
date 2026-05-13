package ui.components.profilebar

import domain.user.model.User

sealed class ProfileBarState {
    data object Idle : ProfileBarState()
    data class Success(val userDataInfo: User) : ProfileBarState()
    data class Failed(val message: String) : ProfileBarState()
    data object Loading : ProfileBarState()
}