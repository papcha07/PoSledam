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
    private val _loadingState = MutableStateFlow<Loading>(Loading())
    val loadingState = _loadingState.asStateFlow()

    private val _loginUiState = MutableSharedFlow<RegisterScreenState>()
    val loginUiState = _loginUiState.asSharedFlow()

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
}