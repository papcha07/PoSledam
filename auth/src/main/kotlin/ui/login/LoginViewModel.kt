package ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.LoginInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ui.model.state.AuthScreenState
import usecases.AuthInteractor

data class Loading(
    val isLoading: Boolean = false
)

class LoginViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {
    private val _loginUiState = MutableSharedFlow<AuthScreenState>()
    val loginUiState = _loginUiState.asSharedFlow()

    fun login(loginInfo: LoginInfo) {
        viewModelScope.launch {
            _loginUiState.emit(AuthScreenState.Loading)
            val loginResult = authInteractor.login(loginInfo)
            val isSuccess = loginResult.first
            if (isSuccess) {
                _loginUiState.emit(AuthScreenState.Success)
                return@launch
            }
            _loginUiState.emit(AuthScreenState.Error("Что-то пошло не так"))
        }
    }
}