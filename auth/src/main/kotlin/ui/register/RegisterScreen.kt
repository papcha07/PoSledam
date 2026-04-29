package ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import kotlinx.coroutines.delay
import ui.components.AuthButton
import ui.components.TextFieldComponent
import ui.components.default_component.SocialTextFieldComponent
import ui.model.data.TextFieldData
import ui.model.data.getContact
import ui.model.state.AuthScreenState
import ui.theme.Primary
import ui.theme.buttonPrimary
import ui.theme.textHint


@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    goToLoginScreen: () -> Unit,
    goPreviewScreen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val registerUiState by registerViewModel.registerUiState.collectAsState("")

        SvgOverlay(Modifier.fillMaxSize())
        RegisterBottomComponent(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(1.8f / 2f),
            registerViewModel = registerViewModel,
            goPreviewScreen = goPreviewScreen

        )

        when (registerUiState) {
            AuthScreenState.Idle -> {}
            AuthScreenState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            AuthScreenState.Success -> goToLoginScreen()
            is AuthScreenState.Error -> {
                AnimatedToast((registerUiState as AuthScreenState.Error).message)
            }
        }
    }
}


@Composable
fun RegisterBottomComponent(
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel,
    goPreviewScreen: () -> Unit
) {

    val currentPage = registerViewModel.currentPage.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = Color.White)
            .padding(top = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .clickable {
                    if (currentPage.value == 0) {
                        goPreviewScreen()
                    } else {
                        registerViewModel.onBackClicker()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_circle_back),
                contentDescription = "back",
                modifier = Modifier.size(40.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .clickable {
                    goPreviewScreen()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "close",
                modifier = Modifier.size(40.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            when (currentPage.value) {
                0 -> {
                    UserInformationComponent(registerViewModel = registerViewModel)
                }

                1 -> {
                    SocialMediaComponent(registerViewModel = registerViewModel)
                }
            }
            Spacer(Modifier.height(52.dp))

            AuthButton(
                color = buttonPrimary,
                text = if (currentPage.value == 1) "Завершить" else "Продолжить",
                textColor = Color.White
            ) {
                when (currentPage.value) {
                    1 -> {
                        registerViewModel.registerUser()
                    }

                    else -> {
                        registerViewModel.onNextClicked()
                    }
                }
            }
        }
    }
}

@Composable
fun UserInformationComponent(
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel
) {
    val userState = registerViewModel.userDataInfoState.collectAsState()
    Column(
        modifier = modifier
            .padding(top = 70.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Представьтесь",
            fontSize = 24.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Расскажите о себе",
            color = textHint,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextFieldComponent(
                value = userState.value.email,
                textFieldData = TextFieldData("Почта", "Введите почту"),
                onValueChange = registerViewModel::setEmail
            )
            Spacer(Modifier.height(8.dp))
            TextFieldComponent(
                value = userState.value.password,
                textFieldData = TextFieldData("Пароль", "Введите пароль"),
                onValueChange = registerViewModel::setPassword
            )
            Spacer(Modifier.height(8.dp))
            TextFieldComponent(
                value = userState.value.name,
                textFieldData = TextFieldData("Ваше имя", "Введите имя"),
                onValueChange = registerViewModel::setName
            )
            Spacer(Modifier.height(8.dp))
            TextFieldComponent(
                value = userState.value.description,
                textFieldData = TextFieldData("Описание", "Напишите небольшой текст о себе"),
                onValueChange = registerViewModel::setDescription
            )
        }
    }
}

@Composable
fun SocialMediaComponent(
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel
) {
    val userState = registerViewModel.userDataInfoState.collectAsState()

    Column(
        modifier = modifier
            .padding(top = 70.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Как с вами связаться",
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Добавьте ссылки чтобы люди могли вам написать",
            color = textHint,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        SocialTextFieldComponent(
            value = userState.value.getContact(0),
            textFieldData = ui.components.default_component.TextFieldData(
                "",
                "Вставьте ссылку на VK"
            ),
            onValueChange = registerViewModel::addVk,
            icon = R.drawable.ic_vk
        )
        Spacer(Modifier.height(8.dp))
        SocialTextFieldComponent(
            value = userState.value.getContact(1),
            textFieldData = ui.components.default_component.TextFieldData(
                "",
                "Вставьте ссылку на Telegram"
            ),
            onValueChange = registerViewModel::addTelegram,
            icon = R.drawable.ic_tg
        )
        Spacer(Modifier.height(8.dp))
        SocialTextFieldComponent(
            value = userState.value.getContact(2),
            textFieldData = ui.components.default_component.TextFieldData(
                "",
                "Вставьте ссылку на Whatsapp"
            ),
            onValueChange = registerViewModel::addWhatsApp,
            icon = R.drawable.ic_whatsapp
        )
    }


}

@Composable
fun SvgOverlay(modifier: Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .background(color = Primary)
    ) {
        Image(
            modifier = Modifier.padding(top = 12.dp),
            painter = painterResource(id = R.drawable.meow_background),
            contentDescription = "meow",
            contentScale = ContentScale.Crop,
        )

        Image(
            painter = painterResource(id = R.drawable.dogs),
            contentDescription = "dogs",
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(top = 96.dp),
        )
    }
}

@Composable
fun AnimatedToast(
    message: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFE3F2FD),
    textColor: Color = Color.Black,
    duration: Long = 2500L,
    onDismiss: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(message) {
        visible = true
        delay(duration)
        visible = false
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        exit = fadeOut() + slideOutVertically { it / 2 },
        enter = fadeIn() + slideInVertically { it / 2 }
    ) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .align(Alignment.Center)
            ) {
                Text(
                    text = message,
                    color = textColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}
