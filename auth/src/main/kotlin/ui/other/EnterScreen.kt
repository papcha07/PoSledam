package ui.other

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.core.R
import ui.components.AuthButton
import ui.register.RegisterViewModel
import ui.theme.Primary
import ui.theme.buttonPrimary
import ui.theme.deleteButtonColor

@Composable
fun EnterScreen(
    navigateToLoginScreen: () -> Unit,
    navigateToRegisterScreen: () -> Unit,
    registerViewModel: RegisterViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = deleteButtonColor)
    ) {
        val (imagebox, bottomBar) = createRefs()

        SvgOverlay(
            modifier = Modifier.constrainAs(imagebox) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(bottomBar.top)
            }
        )

        CardPreviewComponent(
            modifier = Modifier.constrainAs(bottomBar) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            registerViewModel = registerViewModel,
            goToRegisterScreen = {
                navigateToRegisterScreen()
            },
            goToLoginScreen = {
                navigateToLoginScreen()
            }
        )
    }
}


@Composable
fun SvgOverlay(modifier: Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .background(color = deleteButtonColor)
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
fun CardPreviewComponent(
    modifier: Modifier,
    registerViewModel: RegisterViewModel,
    goToRegisterScreen: () -> Unit,
    goToLoginScreen: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 22.dp)
        ) {
            Text(
                text = "Ваш надежный\nпомощник",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ваш пропавший питомец может оказаться здесь или помочь найти дом для найденного питомца",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(44.dp))

            AuthButton(
                color = buttonPrimary,
                text = "Войти",
                textColor = Color.White
            ) {
                goToLoginScreen()
            }
            Spacer(Modifier.height(16.dp))
            AuthButton(
                color = Color(0xFFF5F5F5),
                text = "Зарегистрироваться",
                textColor = Color.Black
            ) {
                registerViewModel.resetPage()
                goToRegisterScreen()
            }

        }
    }
}

