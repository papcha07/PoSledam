package ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import domain.model.LoginInfo
import org.koin.androidx.compose.koinViewModel
import ui.components.ButtonComponent
import ui.components.TextFieldComponent
import ui.model.RegisterScreenState
import ui.model.TextFieldData
import ui.register.AnimatedToast
import ui.theme.EnterOverlayColor
import ui.theme.buttonPrimary

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
    goToMainProfile: () -> Unit
) {
    val loadingState = viewModel.loadingState.collectAsState()
    var toastMessage by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        viewModel.loginUiState.collect { state ->
            when (state) {
                is RegisterScreenState.Error -> {
                    toastMessage = state.message
                }

                RegisterScreenState.Loading -> {

                }

                RegisterScreenState.Success -> {
                    goToMainProfile()
                }

                else -> {

                }

            }
        }
    }

    Scaffold(modifier = modifier.background(color = Color.White), containerColor = Color.White)
    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color.White)
        ) {
            LoginBackground(Modifier.fillMaxSize())
            EnterBottomComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(2.4f / 4f),
                onLogin = { info ->
                    viewModel.login(info)
                },
                googleEnter = {

                }
            )
            toastMessage?.let { msg ->
                AnimatedToast(
                    message = msg,
                    backgroundColor = Color(0xFFCE93D8),
                    textColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 48.dp),
                    onDismiss = {
                        toastMessage = null
                    }
                )
            }
            if (loadingState.value.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

}


@Composable
fun LoginBackground(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .background(EnterOverlayColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.tree_left),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 18.dp, top = 20.dp)
                    .align(Alignment.TopStart)

            )
            Image(
                painter = painterResource(R.drawable.tree_right),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .align(Alignment.TopEnd)
            )
        }

        Image(
            painter = painterResource(R.drawable.dogs_background),
            contentDescription = "dogs_background",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.padding(top = 60.dp)
        )
    }
}

@Composable
fun EnterBottomComponent(
    modifier: Modifier = Modifier,
    onLogin: (LoginInfo) -> Unit,
    googleEnter: () -> Unit,
) {
    val textFields = listOf(
        TextFieldData(
            label = "Почта",
            hint = "Введите email"
        ),
        TextFieldData(
            label = "Пароль",
            hint = "Введите пароль"
        )
    )
    val values = remember { textFields.map { mutableStateOf("") } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            text = "Вход",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            text = "Введите почту и пароль",
            fontSize = 16.sp,
            color = Color(0xFFA8A8A8)
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            textFields.forEachIndexed { index, data ->
                TextFieldComponent(
                    value = values[index].value,
                    textFieldData = data,
                ) {
                    values[index].value = it
                }
                Spacer(Modifier.height(8.dp))
            }
            ButtonComponent(
                modifier = Modifier.height(54.dp),
                color = buttonPrimary,
                text = "Войти",
                textColor = Color.White,
                enabled = values.all { state ->
                    state.value.isNotBlank()
                },
                radius = 15.dp,
                onClick = {
                    onLogin(
                        LoginInfo(
                            email = values[0].value,
                            password = values[1].value
                        )
                    )
                }
            )
            Spacer(Modifier.height(32.dp))
            LineComponent()
            Spacer(Modifier.height(32.dp))
            GoogleButtonComponent(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    googleEnter()
                }
            )
            Spacer(Modifier.height(30.dp))
            PoliticTextComponent()
        }
    }
}

@Composable
fun LineComponent(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.line),
            contentDescription = null
        )
        Text(
            text = "или",
            fontSize = 16.sp,
            color = Color(0xFF686868)
        )
        Image(
            painter = painterResource(R.drawable.line),
            contentDescription = null
        )
    }
}

@Composable
fun GoogleButtonComponent(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEAEAEA)
        ),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(20.dp),
        modifier = modifier
            .size(56.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = "Google icon",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun PoliticTextComponent(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append("Продолжая, вы принимаете условия \n")
            withStyle(style = SpanStyle(color = Color(0xFF686868))) { // здесь меняем цвет
                append("\t\tполитики конфиденциальности")
            }
        },
        fontSize = 14.sp
    )
}


@Preview
@Composable
private fun LoginBackgroundPreview() {
    LoginBackground(modifier = Modifier)
}