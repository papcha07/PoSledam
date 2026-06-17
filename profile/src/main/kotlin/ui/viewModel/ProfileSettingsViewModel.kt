package ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.loader.ImageLoaderInteractor
import domain.notification.NotificationInteractor
import domain.notification.NotificationSettingsInteractor
import domain.user.UserInteractor
import domain.user.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.components.profilebar.ProfileBarState


class ProfileSettingsViewModel(
    private val userInteractor: UserInteractor,
    private val notificationSettingsInteractor: NotificationSettingsInteractor,
    private val imageLoaderInteractor: ImageLoaderInteractor,
    private val notificationInteractor: NotificationInteractor,
) : ViewModel() {


    private val _profileInfoState = MutableStateFlow(User())
    val profileInfoState = _profileInfoState.asStateFlow()

    private val _userInfoState = MutableStateFlow<ProfileBarState>(ProfileBarState.Idle)
    val userInfoState = _userInfoState.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private var observeUserJob: Job? = null

    init {
        viewModelScope.launch {
            _notificationsEnabled.value = notificationSettingsInteractor.isNotificationsEnabled()
        }
    }


    fun setName(name: String) {
        _profileInfoState.update {
            it.copy(name = name)
        }
    }

    fun setDescription(description: String) {
        _profileInfoState.update {
            it.copy(description = description)
        }
    }

    fun addVk(url: String) {
        _profileInfoState.update {
            it.copy(vk = url)
        }
    }

    fun addWhatsApp(url: String) {
        _profileInfoState.update {
            it.copy(wh = url)
        }
    }

    fun addTelegram(url: String) {
        _profileInfoState.update {
            it.copy(tg = url)
        }
    }

    fun observeUser() {
        if (observeUserJob?.isActive == true) return

        observeUserJob = viewModelScope.launch {
            _userInfoState.value = ProfileBarState.Loading
            userInteractor.observeUser().collect { user ->
                if (user == null) {
                    _userInfoState.value = ProfileBarState.Loading
                } else {
                    _profileInfoState.value = user
                    _userInfoState.value = ProfileBarState.Success(user)
                }
            }
        }
    }

    fun updateUserInfo() {
        viewModelScope.launch {
            userInteractor.updateUserInfo(_profileInfoState.value)
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
            userInteractor.clearUser()
            notificationInteractor.deleteAll()
        }
    }

    fun setUri(uri: String) {
        _profileInfoState.update {
            it.copy(avatarPath = uri)
        }
    }

    fun updateImage() {
        viewModelScope.launch {
            val uri = _profileInfoState.value.avatarPath!!
            val id = _profileInfoState.value.id
            imageLoaderInteractor.loadImage(uri, id)
        }
    }
}
