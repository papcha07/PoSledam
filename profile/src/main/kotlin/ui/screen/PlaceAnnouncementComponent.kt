package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yandex.mapkit.geometry.Point
import ui.components.PetParameterComponent
import ui.components.ProfileMap
import ui.components.announcement.EventDateComponent
import ui.components.slider.PhotosPager
import ui.theme.Ser
import ui.viewModel.ActionViewModel

@Composable
fun PlaceAnnouncementComponent(
    modifier: Modifier = Modifier,
    actionViewModel: ActionViewModel
) {
    val announcementState = actionViewModel.state.collectAsState()
    val formFillState by actionViewModel.isFormValidState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Проверьте объявление",
                fontSize = 15.sp
            )
            Spacer(Modifier.height(16.dp))
            PhotosPager(
                photos = announcementState.value.selectedImageUris,
                onAddPhotoClick = { }
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Особые приметы",
                fontSize = 16.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = announcementState.value.description,
                fontSize = 14.sp,
                color = Ser
            )
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PetParameterComponent(
                    type = "Порода",
                    value = announcementState.value.breed
                )
                PetParameterComponent(
                    type = "Пол",
                    value = if (announcementState.value.gender == 0) "мальчик" else "девочка"
                )
                PetParameterComponent(
                    type = "Окрас",
                    value = announcementState.value.color
                )
            }
            Spacer(Modifier.height(24.dp))
            EventDateComponent(
                advertState = announcementState.value.formattedDateTime
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Где потерялся",
                fontSize = 16.sp
            )
            Spacer(Modifier.height(12.dp))
            ProfileMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                pointClick = { lon: Double, lat: Double ->

                },
                myLocation = Point(announcementState.value.lat!!, announcementState.value.lon!!)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = actionViewModel.addressText
            )
            Spacer(Modifier.height(20.dp))
            CreateAnnouncement(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = formFillState
            ) {
                    actionViewModel.createAnnouncement()
            }

        }

    }
}

