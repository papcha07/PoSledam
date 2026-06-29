package ui.screen.street.detailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.components.placeholder.ShimmerLoadingTransition
import ui.components.placeholder.ShimmerTextPlaceholder
import ui.components.streetPager.StreetPhotoPager
import ui.model.ScreenState
import ui.screen.street.StreetPetViewModel
import ui.screen.street.detailsScreen.component.DescriptionComponent
import ui.theme.backgroundColor
import ui.theme.eventDateComponentColor


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
    ShimmerLoadingTransition(
        isLoading = detailsState is ScreenState.Loading,
        modifier = modifier.fillMaxSize(),
        loadingContent = {
            StreetDetailsShimmerPlaceholder(modifier = Modifier.fillMaxSize())
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val alignModifier = Modifier.align(Alignment.Center)
            when (detailsState) {
                ScreenState.Error -> {
                    ErrorPlaceholder(modifier = alignModifier)
                }

                ScreenState.Idle,
                ScreenState.Loading -> Unit

                ScreenState.InternetError -> {
                    ErrorPlaceholder(modifier = alignModifier)
                }

                is ScreenState.Success<StreetDetails> -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
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
}

@Composable
private fun StreetDetailsShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        ShimmerImagePlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.46f)
            )
            Spacer(Modifier.height(12.dp))
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.92f)
            )
            Spacer(Modifier.height(8.dp))
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.68f)
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(eventDateComponentColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerImagePlaceholder(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(0.36f)
                    )
                    Spacer(Modifier.height(6.dp))
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.58f)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.28f)
            )
            Spacer(Modifier.height(12.dp))
            ShimmerImagePlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.height(24.dp))
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
        EventDateComponent(advertState = streetDetails.dateInfo, announcementType = 0)
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
