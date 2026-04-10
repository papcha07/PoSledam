package ui.streetScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.core.R
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.components.other.NearPetCardComponent
import ui.components.street.StreetGridPets
import ui.theme.backgroundColor

@Composable
fun StreetPetScreen(
    modifier: Modifier = Modifier,
    streetPetViewModel: StreetPetViewModel
) {
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
            onBackClick = {

            },
            onActionClick = {

            }
        )
        Spacer(Modifier.height(10.dp))
        StreetPetSection(streetPetViewModel = streetPetViewModel)

    }
}

@Composable
fun StreetPetSection(
    modifier: Modifier = Modifier,
    streetPetViewModel: StreetPetViewModel
) {
    LaunchedEffect(Unit) {
        streetPetViewModel.getStreetAnimals()
    }

    val streetAnimalsState = streetPetViewModel.animalScreenState.collectAsState()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = Color.White)
            .fillMaxHeight()
    ) {
        Column(
            Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (streetAnimalsState.value) {
                StreetPetScreenState.Empty -> {

                }

                StreetPetScreenState.Failed -> {

                }

                StreetPetScreenState.Idle -> {

                }

                is StreetPetScreenState.Success -> {
                    val animalList = (streetAnimalsState.value as StreetPetScreenState.Success).data
                    if (animalList.size > 1) {
                        NearPetCardComponent(
                            streetPetPreviewModel = animalList.last()
                        ) {

                        }
                        Spacer(Modifier.height(16.dp))
                        StreetGridPets(
                            animalList = animalList.subList(0, animalList.lastIndex)
                        )
                    } else {
                        NearPetCardComponent(
                            streetPetPreviewModel = animalList.last()
                        ) {

                        }
                    }

                }
            }

        }
    }
}

//@Preview
//@Composable
//private fun StreetPetSectionPreview() {
//    StreetPetSection()
//}