package ui.screen.street

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.components.BackCircleButton
import ui.components.ButtonComponent
import ui.components.default_component.AnimatedToast
import ui.components.placeholder.SuccessSendPopup
import ui.screen.camera.CameraViewModel
import ui.theme.backgroundColor
import ui.theme.buttonPrimary

@Composable
fun AddStreetAnimalScreen(
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel,
    onBack: () -> Unit,
    onPublished: () -> Unit = onBack
) {
    val advertState by cameraViewModel.advertState.collectAsState()
    val urisState = cameraViewModel.uris.collectAsState()
    val scrollState = rememberScrollState()
    val pokemonCardStats = remember { PokemonCardStats.roll() }

    LaunchedEffect(cameraViewModel) {
        cameraViewModel.prepareAdvertForPublishing()
    }
    BackHandler {
        onBack()
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .background(color = backgroundColor)
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Spacer(Modifier.height(76.dp))
            PokemonStreetCard(
                modifier = Modifier.padding(horizontal = 14.dp),
                photos = urisState.value,
                addDescription = cameraViewModel::addDescription,
                advertState = advertState,
                cardStats = pokemonCardStats
            )
            Spacer(Modifier.height(96.dp))
        }
        PublishButtonRow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onClick = cameraViewModel::createStreetAdvert
        )

        SuccessSendPopup(
            visible = advertState.isPlaced,
            title = "Объявление отправлено",
            description = "Спасибо что отметили животное!\nЭто поможет найти ему дом.",
            onDismiss = onPublished,
        )

        if (advertState.internetError) {
            AnimatedToast(message = "Проблемы с интернетом")
        }

        if (advertState.serverError) {
            AnimatedToast(message = "Что-то пошло не так")
        }

        BackCircleButton(
            onBack = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 30.dp, start = 30.dp)
        )

        if (advertState.isLoading) {
            AddStreetAnimalLoadingOverlay(modifier = Modifier.fillMaxSize())
        }
    }

}

@Composable
private fun AddStreetAnimalLoadingOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = buttonPrimary)
    }
}

@Composable
private fun PublishButtonRow(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        ButtonComponent(
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth(),
            color = buttonPrimary,
            text = "Отправить",
            textColor = Color.White,
            enabled = true,
            radius = 12.dp,
            onClick = onClick
        )
    }
}
