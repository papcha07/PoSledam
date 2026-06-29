package ui.email_confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.R
import domain.model.AuthOperationErrorType
import domain.model.AuthOperationResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import usecases.AuthInteractor

class EmailConfirmationViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmailConfirmationUiState())
    val uiState = _uiState.asStateFlow()

    private var cooldownJob: Job? = null

    fun setEmail(email: String) {
        val cleanEmail = email.trim()
        if (_uiState.value.email == cleanEmail) return

        _uiState.update {
            it.copy(
                email = cleanEmail,
                errorMessageRes = if (cleanEmail.isBlank()) {
                    R.string.email_confirmation_missing_email
                } else {
                    null
                },
                successMessageRes = null
            )
        }
    }

    fun resendEmailConfirmation() {
        val state = _uiState.value
        if (!state.canResend) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageRes = null,
                    successMessageRes = null
                )
            }

            when (val result = authInteractor.resendEmailConfirmation(state.email)) {
                AuthOperationResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessageRes = R.string.email_confirmation_sent_again
                        )
                    }
                    startCooldown()
                }

                is AuthOperationResult.Error -> {
                    val message = when (result.type) {
                        AuthOperationErrorType.TooManyEmailConfirmationRequests ->
                            R.string.email_confirmation_too_many_requests

                        AuthOperationErrorType.NoInternet ->
                            R.string.email_confirmation_no_internet

                        AuthOperationErrorType.NotFound ->
                            R.string.email_confirmation_email_not_found

                        AuthOperationErrorType.Unknown ->
                            R.string.email_confirmation_unknown_error
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = message
                        )
                    }

                    if (result.type == AuthOperationErrorType.TooManyEmailConfirmationRequests) {
                        startCooldown()
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessageRes = null,
                successMessageRes = null
            )
        }
    }

    private fun startCooldown(seconds: Int = RESEND_COOLDOWN_SECONDS) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (remaining in seconds downTo 1) {
                _uiState.update { it.copy(resendCooldownSeconds = remaining) }
                delay(1_000L)
            }
            _uiState.update { it.copy(resendCooldownSeconds = 0) }
        }
    }

    override fun onCleared() {
        cooldownJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val RESEND_COOLDOWN_SECONDS = 60
    }
}
