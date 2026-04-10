package ui.components.profilebar

import ui.model.UserDataUiInfo

sealed class ProfileBarState {
    data object Idle : ProfileBarState()
    data class Success(val userDataInfo: UserDataUiInfo) : ProfileBarState()
    data class Failed(val message: String) : ProfileBarState()
    data object Loading : ProfileBarState()
}