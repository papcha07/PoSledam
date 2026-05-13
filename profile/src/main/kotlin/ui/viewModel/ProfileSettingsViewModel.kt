package ui.viewModel

import ApiResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.notification.NotificationSettingsInteractor
import domain.interactor.main.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.components.profilebar.ProfileBarState
import ui.model.UserDataUiInfo


class ProfileSettingsViewModel(
    private val mainInteractor: MainInteractor,
    private val notificationSettingsInteractor: NotificationSettingsInteractor
) : ViewModel() {


    private val _profileInfoState = MutableStateFlow(UserDataUiInfo())
    val profileInfoState = _profileInfoState.asStateFlow()

    private val _userInfoState = MutableStateFlow<ProfileBarState>(ProfileBarState.Idle)
    val userInfoState = _userInfoState.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            _notificationsEnabled.value = notificationSettingsInteractor.isNotificationsEnabled()
        }
    }

    private fun updateProfile(transform: (UserDataUiInfo) -> UserDataUiInfo) {
        _profileInfoState.update(transform)
        scheduleDebouncedUpdate()
    }

    private fun scheduleDebouncedUpdate() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(900L)
            try {
                mainInteractor.updateUserInfo(_profileInfoState.value)
            } catch (_: Exception) {
            }
        }
    }

    fun setName(name: String) {
        updateProfile { it.copy(name = name) }
    }

    fun setDescription(description: String) {
        updateProfile { it.copy(description = description) }
    }

    fun addVk(url: String) = addContact(0, url)
    fun addWhatsApp(url: String) = addContact(1, url)
    fun addTelegram(url: String) = addContact(2, url)

    private fun addContact(contactType: Int, url: String) {
        updateProfile { userDataInfo ->
            val updated = userDataInfo.contacts.toMutableList()

            if (url.isBlank()) {
                updated.removeAll { it.contactType == contactType }
            } else {
                val index = updated.indexOfFirst { it.contactType == contactType }

                if (index >= 0) {
                    updated[index] = updated[index].copy(url = url)
                } else {
                    updated.add(UserDataUiInfo.ContactType(contactType, url))
                }
            }

            userDataInfo.copy(contacts = updated)
        }
    }

    fun loadUser() {
        _userInfoState.update {
            ProfileBarState.Loading
        }
        viewModelScope.launch {
            val cachedUser = mainInteractor.getUserFromCache()
            if (cachedUser != null) {
                _userInfoState.update { ProfileBarState.Success(cachedUser) }
                _profileInfoState.value = UserDataUiInfo(
                    id = cachedUser.id,
                    name = cachedUser.name,
                    description = cachedUser.description,
                    contacts = cachedUser.contacts,
                    uri = cachedUser.uri
                )
            } else {
                when (val result = mainInteractor.syncUserFromServer()) {
                    is ApiResponse.Error -> {
                        _userInfoState.update {
                            ProfileBarState.Failed("Не удалось получить информацию о пользователе")
                        }
                    }

                    is ApiResponse.Success<UserDataUiInfo> -> {
                        _userInfoState.update { ProfileBarState.Success(result.data) }
                        _profileInfoState.value = result.data
                    }
                }
            }

        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            notificationSettingsInteractor.setNotificationsEnabled(enabled)
            _notificationsEnabled.value = enabled
        }
    }


    fun logout() {
        viewModelScope.launch {
            mainInteractor.deleteUser()
        }
    }

    fun setUri(uri: String) {
        updateProfile { it.copy(uri = uri) }
    }

    fun updateImage() {
        val uri = _profileInfoState.value.uri
        val id = _profileInfoState.value.id
        if (uri != null) {
            viewModelScope.launch {
                mainInteractor.updateUserImage(uri, id)

                when (val result = mainInteractor.syncUserFromServer()) {
                    is ApiResponse.Success -> {
                        _profileInfoState.update {
                            result.data
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }

}