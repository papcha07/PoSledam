package ui.screen.street

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.R
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.components.other.NearPetCardComponent
import ui.components.placeholder.EmptyAnimalList
import ui.components.placeholder.ErrorPlaceholder
import ui.components.street.StreetGridPets
import ui.theme.backgroundColor

@Composable
fun StreetPetScreen(
    modifier: Modifier = Modifier,
    streetPetViewModel: StreetPetViewModel,
    returnToMainScreen: () -> Unit,
    openFilterSettings: () -> Unit
) {
    LaunchedEffect(Unit) {
        streetPetViewModel.getStreetAnimals()
    }

    val streetAnimalsState by streetPetViewModel.animalScreenState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {

        ToolBar(
            toolBarInfo = ToolBarInfo(
                title = "Замеченные питомцы",
                backArrow = true,
                backArrowIcon = R.drawable.left_arrow,
                actionIcon = R.drawable.ic_settings,
            ),
            onBackClick = returnToMainScreen,
            onActionClick = openFilterSettings
        )
        Spacer(Modifier.height(10.dp))
        StreetPetSection(streetPetScreenState = streetAnimalsState)
    }
}

@Composable
fun StreetPetSection(
    modifier: Modifier = Modifier,
    streetPetScreenState: StreetPetScreenState
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White)
            .fillMaxSize()
    ) {
        val centerModifier = Modifier.align(Alignment.Center)

        when (streetPetScreenState) {
            StreetPetScreenState.Empty -> {
                EmptyAnimalList(modifier = centerModifier)
            }

            StreetPetScreenState.Failed -> {
                ErrorPlaceholder(modifier = centerModifier)
            }

            StreetPetScreenState.Idle -> Unit

            StreetPetScreenState.Loading -> {
                CircularProgressIndicator(modifier = centerModifier)
            }

            is StreetPetScreenState.Success -> {
                val animalList = streetPetScreenState.data

                if (animalList.isEmpty()) {
                    EmptyAnimalList(modifier = centerModifier)
                    return@Box
                }

                val nearestPet = animalList.last()
                val otherPets = animalList.dropLast(1)

                Column(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NearPetCardComponent(
                        streetPetPreviewModel = nearestPet,
                        navigateToStreetPetScreen = {
                        }
                    )

                    if (otherPets.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        StreetGridPets(animalList = otherPets)
                    }
                }
            }
        }
    }
}
