package ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.AnnouncementInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.model.PetUiPreview

sealed class ProfileScreenState {
    data class Success(val petList: List<PetUiPreview>) : ProfileScreenState()
    data object Loading : ProfileScreenState()
    data object Failed : ProfileScreenState()
    data object Empty : ProfileScreenState()
    data object Idle : ProfileScreenState()
}

class ProfileViewModel(
    private val announcementInteractor: AnnouncementInteractor
) : ViewModel() {

    private val _userPetState = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Idle)
    val userPetState = _userPetState.asStateFlow()

    private val _userMethodState = MutableStateFlow<Int>(0)
    val userMethodState: StateFlow<Int> = _userMethodState.asStateFlow()

    fun getAnimalList() {
        viewModelScope.launch {
            _userPetState.update {
                ProfileScreenState.Loading
            }
            val result = announcementInteractor.getUserAnnouncements(_userMethodState.value)
            val animalList = result.first
            if (animalList != null) {
                _userPetState.update {
                    if (animalList.isNotEmpty()) ProfileScreenState.Success(animalList) else ProfileScreenState.Empty
                }
            } else {
                _userPetState.update {
                    ProfileScreenState.Failed
                }
            }
        }
    }

    fun updateMethodValue(value: Int) = _userMethodState.update {
        value
    }
}