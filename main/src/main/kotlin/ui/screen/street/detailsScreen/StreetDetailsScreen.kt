package ui.screen.street.detailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yandex.mapkit.geometry.Point
import domain.models.StreetDetails
import ui.components.BackCircleButton
import ui.components.CurrentLocationMap
import ui.components.EventDateComponent
import ui.components.placeholder.ErrorPlaceholder
import ui.components.streetPager.StreetPhotoPager
import ui.model.ScreenState
import ui.screen.street.StreetPetViewModel
import ui.screen.street.detailsScreen.component.DescriptionComponent
import ui.theme.backgroundColor


@Composable
fun StreetPetDetailRouter(
    modifier: Modifier = Modifier,
    streetPetViewModel: StreetPetViewModel,
    animalId: String,
    returnBack: () -> Unit
) {
    LaunchedEffect(animalId) {
        streetPetViewModel.getDetailsAboutAnimal(animalId)
    }
    val detailsState by streetPetViewModel.detailsState.collectAsStateWithLifecycle()

    StreetDetailsScreen(
        detailsState = detailsState,
        returnBack = returnBack
    )
}

@Composable
fun StreetDetailsScreen(
    modifier: Modifier = Modifier,
    detailsState: ScreenState<StreetDetails>,
    returnBack: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        val alignModifier = Modifier.align(Alignment.Center)
        when (detailsState) {
            ScreenState.Error -> {
                ErrorPlaceholder(modifier = alignModifier)
            }

            ScreenState.Idle -> {}
            ScreenState.InternetError -> {
                ErrorPlaceholder(modifier = alignModifier)
            }

            ScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is ScreenState.Success<StreetDetails> -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = modifier
                            .background(color = backgroundColor)
                            .fillMaxSize()
                    ) {
                        StreetPhotoPager(
                            photos = detailsState.data.imagePath.map {
                                it.toUri()
                            }
                        )
                        StreetDetailsBodyComponent(
                            streetDetails = detailsState.data,
                        )
                    }
                    BackCircleButton(
                        onBack = returnBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 30.dp, start = 30.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun StreetDetailsBodyComponent(
    modifier: Modifier = Modifier,
    streetDetails: StreetDetails,
) {
    Column(
        modifier = modifier
            .background(color = Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxSize()
    ) {
        Spacer(Modifier.height(4.dp))
        DescriptionComponent(
            placeDescription = streetDetails.placeDescription
        )
        Spacer(Modifier.height(32.dp))
        EventDateComponent(advertState = streetDetails.dateInfo)
        Spacer(Modifier.height(32.dp))
        CurrentLocationMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            currentLocation = Point(
                streetDetails.lat,
                streetDetails.lon
            ),
            onLocationResolved = { lat, lon ->
            }
        )
    }
}
