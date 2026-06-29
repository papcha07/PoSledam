package ui.email_confirmation

import androidx.annotation.StringRes

data class EmailConfirmationUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null,
    val resendCooldownSeconds: Int = 0
) {
    val canResend: Boolean
        get() = email.isNotBlank() && !isLoading && resendCooldownSeconds == 0
}
