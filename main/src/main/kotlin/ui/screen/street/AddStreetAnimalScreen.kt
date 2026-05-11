package ui.screen.street

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yandex.mapkit.geometry.Point
import domain.models.AdvertInfo
import ui.components.BackCircleButton
import ui.components.ButtonComponent
import ui.components.CurrentLocationMap
import ui.components.EventDateComponent
import ui.components.other.TextFieldComponent
import ui.components.placeholder.SuccessSendPopup
import ui.components.streetPager.StreetPhotoPager
import ui.model.data.TextFieldData
import ui.register.AnimatedToast
import ui.screen.camera.CameraViewModel
import ui.theme.addressText
import ui.theme.backgroundColor
import ui.theme.buttonPrimary

@Composable
fun AddStreetAnimalScreen(
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel,
    onBack: () -> Unit
) {
    val advertState by cameraViewModel.advertState.collectAsState()
    val urisState = cameraViewModel.uris.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        cameraViewModel.loadMyLocation()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .background(color = backgroundColor)
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            StreetPhotoPager(
                photos = urisState.value,
            )
            Spacer(Modifier.height(4.dp))
            InformationComponent(
                addDescription = cameraViewModel::addDescription,
                advertState = advertState,
            )
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
            onDismiss = onBack,
        )

        if (advertState.internetError) {
            AnimatedToast(message = "Проблемы с интернетом")
        }

        if (advertState.serverError) {
            AnimatedToast(message = "Что-то пошло не так")
        }

        if (advertState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        BackCircleButton(
            onBack = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 30.dp, start = 30.dp)
        )
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
            .padding(top = 16.dp, bottom = 34.dp),
        contentAlignment = Alignment.Center
    ) {
        ButtonComponent(
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth(),
            color = buttonPrimary,
            text = "Опубликовать",
            textColor = Color.White,
            enabled = true,
            radius = 12.dp,
            onClick = onClick
        )
    }
}

@Composable
fun InformationComponent(
    modifier: Modifier = Modifier,
    addDescription: (String) -> Unit,
    advertState: AdvertInfo
) {

    Box(
        modifier = modifier
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Описание места",
                fontSize = 16.sp
            )
            Spacer(Modifier.height(10.dp))
            TextFieldComponent(
                value = advertState.placeDescription,
                textFieldData = TextFieldData(
                    label = "Описание",
                    hint = "Введите описание места"
                ),
                onValueChange = addDescription
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Где нашли?",
                fontSize = 16.sp
            )
            Spacer(Modifier.height(12.dp))
            CurrentLocationMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                currentLocation = Point(
                    advertState.lat,
                    advertState.lon
                ),
                onLocationResolved = { lat, lon ->
                }
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = advertState.address,
                fontSize = 14.sp,
                color = addressText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(20.dp))
            EventDateComponent(advertState = advertState.eventDate)
        }
    }
}
