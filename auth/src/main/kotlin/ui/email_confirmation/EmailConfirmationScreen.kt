package ui.email_confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.R
import org.koin.androidx.compose.koinViewModel
import ui.OverlayBlackout
import ui.components.AuthButton
import ui.components.AuthButtonComponent
import ui.components.default_component.AnimatedToast
import ui.register.SvgOverlay
import ui.theme.buttonPrimary
import ui.theme.textHint

@Composable
fun EmailConfirmationRoute(
    email: String,
    viewModel: EmailConfirmationViewModel = koinViewModel(),
    goToLoginScreen: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(email) {
        viewModel.setEmail(email)
    }

    EmailConfirmationScreen(
        state = state,
        onResendClick = viewModel::resendEmailConfirmation,
        onGoToLoginClick = goToLoginScreen,
        onToastDismiss = viewModel::clearMessages
    )
}

@Composable
fun EmailConfirmationScreen(
    state: EmailConfirmationUiState,
    onResendClick: () -> Unit,
    onGoToLoginClick: () -> Unit,
    onToastDismiss: () -> Unit
) {
    val errorMessage = state.errorMessageRes?.let { stringResource(it) }
    val successMessage = state.successMessageRes?.let { stringResource(it) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SvgOverlay(Modifier.fillMaxSize())

        EmailConfirmationBottomComponent(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(1.8f / 2f),
            state = state,
            onResendClick = onResendClick,
            onGoToLoginClick = onGoToLoginClick
        )

        when {
            errorMessage != null -> {
                AnimatedToast(
                    message = errorMessage,
                    onDismiss = onToastDismiss
                )
            }

            successMessage != null -> {
                AnimatedToast(
                    message = successMessage,
                    onDismiss = onToastDismiss
                )
            }
        }

        if (state.isLoading) {
            OverlayBlackout(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun EmailConfirmationBottomComponent(
    modifier: Modifier = Modifier,
    state: EmailConfirmationUiState,
    onResendClick: () -> Unit,
    onGoToLoginClick: () -> Unit
) {
    val resendText = if (state.resendCooldownSeconds > 0) {
        stringResource(
            R.string.email_confirmation_resend_timer,
            state.resendCooldownSeconds
        )
    } else {
        stringResource(R.string.email_confirmation_resend)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 70.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.email_confirmation_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(
                    R.string.email_confirmation_description,
                    state.email
                ),
                color = textHint,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(32.dp))
            AuthButtonComponent(
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth(),
                color = buttonPrimary,
                text = resendText,
                textColor = Color.White,
                enabled = state.canResend,
                radius = 15.dp,
                onClick = onResendClick
            )
            Spacer(Modifier.height(16.dp))
            AuthButton(
                color = Color(0xFFF5F5F5),
                text = stringResource(R.string.email_confirmation_to_login),
                textColor = Color.Black,
                onClick = onGoToLoginClick
            )
        }
    }
}
