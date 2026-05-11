package ui.screen.street.detailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import domain.models.StreetDetails
import ui.components.BackCircleButton
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
    animalId: String
) {
    LaunchedEffect(animalId) {
        streetPetViewModel.getDetailsAboutAnimal(animalId)
    }
    val detailsState by streetPetViewModel.detailsState.collectAsStateWithLifecycle()

    StreetDetailsScreen(
        detailsState = detailsState,
        returnBack = {

        }
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
                StreetDetailsBodyComponent(
                    streetDetails = detailsState.data,
                    returnBack = returnBack,
                )
            }
        }

    }
}


@Composable
fun StreetDetailsBodyComponent(
    modifier: Modifier = Modifier,
    streetDetails: StreetDetails,
    returnBack: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .background(color = backgroundColor)
                .fillMaxSize()
        ) {
            StreetPhotoPager(
                photos = streetDetails.imagePath.map {
                    it.toUri()
                }
            )
            Spacer(Modifier.height(4.dp))
            DescriptionComponent(
                placeDescription = streetDetails.placeDescription
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
