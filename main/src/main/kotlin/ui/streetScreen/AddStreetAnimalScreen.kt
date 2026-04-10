package ui.streetScreen

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yandex.mapkit.geometry.Point
import domain.models.AdvertInfo
import ui.cameraScreen.CameraViewModel
import ui.components.ButtonComponent
import ui.components.CurrentLocationMap
import ui.components.EventDateComponent
import ui.components.other.TextFieldComponent
import ui.components.slider.PhotosPager
import ui.model.TextFieldData
import ui.register.AnimatedToast
import ui.theme.addressText
import ui.theme.backgroundColor
import ui.theme.buttonPrimary

@Composable
fun AddStreetAnimalScreen(
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel,
    onBack: () -> Unit
) {
    val urisState = cameraViewModel.uris.collectAsState()
    val advertState = cameraViewModel.advertState.collectAsState()
    var toastMessage by remember { mutableStateOf<String?>(null) }


    when (advertState.value.isPlaced) {
        true -> {
            onBack()
        }

        else -> {

        }
    }

    LaunchedEffect(Unit) {
        cameraViewModel.toastState.collect { message ->
            toastMessage = message
        }
    }

    LaunchedEffect(Unit) {
        cameraViewModel.loadMyLocation()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .background(color = backgroundColor)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PhotosPager(
                modifier = Modifier.height(290.dp),
                photos = urisState.value,
                onAddPhotoClick = onBack,
                onRemovePhotoClick = { uri -> cameraViewModel.removePhoto(uri) }
            )
            Spacer(Modifier.height(4.dp))
            InformationComponent(cameraViewModel = cameraViewModel, advertState = advertState.value)
            Spacer(Modifier.height(16.dp))
            PublishButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = cameraViewModel::createStreetAdvert
            )
        }

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
    }

}

@Composable
private fun PublishButtonRow(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        ButtonComponent(
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
    cameraViewModel: CameraViewModel,
    advertState: AdvertInfo
) {

    Box(
        modifier = modifier
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
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
                onValueChange = cameraViewModel::addDescription
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Где нашли?",
                fontSize = 16.sp
            )
            Spacer(Modifier.height(12.dp))
            val hasLocation = advertState.lat != 32.0 || advertState.lon != 32.0
            CurrentLocationMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp)),
                currentLocation = if (hasLocation) Point(advertState.lat, advertState.lon) else null,
                onLocationResolved = { _, _ -> }
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
