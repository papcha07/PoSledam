package ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.LoginInfo
import domain.model.LoginErrorType
import domain.model.LoginResult
import helper.LocationSyncRequestStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ui.model.state.AuthScreenState
import usecases.AuthInteractor

data class Loading(
    val isLoading: Boolean = false
)

class LoginViewModel(
    private val authInteractor: AuthInteractor,
    private val locationSyncRequestStore: LocationSyncRequestStore
) : ViewModel() {
    private val _loginUiState = MutableSharedFlow<AuthScreenState>()
    val loginUiState = _loginUiState.asSharedFlow()

    fun login(loginInfo: LoginInfo) {
        viewModelScope.launch {
            _loginUiState.emit(AuthScreenState.Loading)
            val loginResult = authInteractor.login(loginInfo)

            when (loginResult) {
                LoginResult.Success -> {
                    locationSyncRequestStore.markSendAfterLoginPending()
                    _loginUiState.emit(AuthScreenState.Success)
                }

                is LoginResult.Error -> {
                    _loginUiState.emit(loginResult.toAuthScreenState(loginInfo.email))
                }
            }
        }
    }

    private fun LoginResult.Error.toAuthScreenState(email: String): AuthScreenState {
        return when (type) {
            LoginErrorType.EmailNotConfirmed ->
                AuthScreenState.EmailNotConfirmed(email.trim())

            LoginErrorType.InvalidCredentials ->
                AuthScreenState.Error("Неверный логин или пароль")

            LoginErrorType.NoInternet ->
                AuthScreenState.Error("Проверьте подключение к интернету")

            LoginErrorType.Server ->
                AuthScreenState.Error("Сервер временно недоступен")

            LoginErrorType.Unknown ->
                AuthScreenState.Error(message?.takeIf { it.isNotBlank() } ?: "Что-то пошло не так")
        }
    }
}
