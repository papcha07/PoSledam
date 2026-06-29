package ui.components.bottom_report

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yandex.mapkit.geometry.Point
import ui.components.ButtonComponent
import ui.components.slider.PhotosPager
import ui.theme.buttonPrimary

@Composable
fun SeenPetBottomSheetContent(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
    buttonState: Boolean,
    loadingState: Boolean,
    cameraLocation: Point? = null,
    updateLongitude: (Double) -> Unit,
    updateLatitude: (Double) -> Unit,
    onSendClick: () -> Unit,
    onAddPhotoClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA))
            .padding(top = 10.dp, bottom = 20.dp)
    ) {
        Column {
            Text(
                text = "Где вы видели питомца?",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Отметьте место на карте и добавьте фото, чтобы владелец быстрее нашёл питомца.",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp,
                lineHeight = 19.sp,
                color = Color(0xFF777777)
            )

            Spacer(modifier = Modifier.height(16.dp))

            BottomMapComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                cameraLocation = cameraLocation,
                updateLongitude = updateLongitude,
                updateLatitude = updateLatitude
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Фото с места",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhotosPager(
                modifier = Modifier.padding(horizontal = 16.dp),
                photos = photos,
                onAddPhotoClick = onAddPhotoClick,
            )

            Spacer(modifier = Modifier.height(20.dp))

            ButtonComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                color = buttonPrimary,
                text = "Отправить владельцу",
                textColor = Color.White,
                enabled = buttonState,
                radius = 40.dp,
                onClick = onSendClick
            )
        }

        if (loadingState) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

}
