package ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.LoginInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.model.RegisterScreenState
import ui.model.UserDataInfo
import usecases.AuthInteractor

data class Loading(
    val isLoading: Boolean = false
)

class AuthViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val _currentPage = MutableStateFlow<Int>(0)
    val currentPage = _currentPage.asStateFlow()

    fun onNextClicked() {
        _currentPage.update { page ->
            (page + 1).coerceAtMost(2)
        }
        Log.d("pageCurrent", _currentPage.value.toString())
    }

    fun onBackClicker() {
        _currentPage.update { page ->
            (page - 1).coerceAtLeast(0)
        }
        Log.d("pageCurrent", _currentPage.value.toString())
    }

    fun resetPage() {
        _currentPage.value = 0
    }

    private val _registerUiState = MutableSharedFlow<RegisterScreenState>()
    val registerUiState = _registerUiState.asSharedFlow()

    private val _loadingState = MutableStateFlow<Loading>(Loading())
    val loadingState = _loadingState.asStateFlow()

    private val _loginUiState = MutableSharedFlow<RegisterScreenState>()
    val loginUiState = _loginUiState.asSharedFlow()

    private val _userDataInfoState = MutableStateFlow(UserDataInfo())
    val userDataInfoState: StateFlow<UserDataInfo> = _userDataInfoState.asStateFlow()

    fun register() {
        Log.d("AuthViewModel", "register() called")
        viewModelScope.launch {
            _loadingState.update {
                it.copy(isLoading = true)
            }
            _registerUiState.emit(RegisterScreenState.Loading)
            authInteractor.register(_userDataInfoState.value).collect { result ->
                val success = result.first
                if (success) {
                    _registerUiState.emit(RegisterScreenState.Success)
                } else {
                    when (result.second) {
                        403 -> showRegisterMessage("Такой аккаунт уже есть")
                        else -> showRegisterMessage("Что-то пошло не так")
                    }
                }
            }
            _loadingState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun login(loginInfo: LoginInfo) {
        viewModelScope.launch {
            _loadingState.update {
                it.copy(true)
            }
            authInteractor.login(loginInfo).collect { pair ->
                val success = pair.first
                if (success) {
                    _loginUiState.emit(RegisterScreenState.Success)
                } else {
                    when (pair.second) {
                        400 -> {
                            _loginUiState.emit(RegisterScreenState.Error("Неверный логин или пароль"))
                        }

                        -1 -> {
                            _loginUiState.emit(RegisterScreenState.Error("Что-то пошло не так"))
                        }
                    }
                }
            }
            _loadingState.update {
                it.copy(false)
            }
        }
    }

    fun setEmail(email: String) {
        _userDataInfoState.update { userDataInfo ->
            userDataInfo.copy(email = email)
        }
    }

    fun setPassword(password: String) {
        _userDataInfoState.update { userDataInfo ->
            userDataInfo.copy(password = password)
        }
    }

    fun setName(name: String) {
        _userDataInfoState.update {
            it.copy(name = name)
        }
    }

    fun setDescription(description: String) {
        _userDataInfoState.update {
            it.copy(description = description)
        }
    }

    fun addVk(url: String) = addContact(0, url)

    fun addTelegram(url: String) = addContact(1, url)

    fun addWhatsApp(url: String) = addContact(2, url)

    private fun addContact(contactType: Int, url: String) {
        _userDataInfoState.update { userDataInfo ->
            val updated = userDataInfo.contacts.toMutableList()

            val index = updated.indexOfFirst { it.contactType == contactType }

            if (index >= 0) {
                updated[index] = updated[index].copy(url = url)
            } else {
                updated.add(UserDataInfo.ContactType(contactType, url))
            }
            userDataInfo.copy(contacts = updated)
        }
    }

    private fun UserDataInfo.getContact(type: Int): String {
        return contacts.firstOrNull { it.contactType == type }?.url ?: ""
    }


    private fun showRegisterMessage(message: String) {
        viewModelScope.launch {
            _registerUiState.emit(RegisterScreenState.Error(message))
        }
    }

}