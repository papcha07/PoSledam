package ui.screen.street

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.core.R
import domain.models.StreetPetPreviewModel
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.components.street.StreetPetGrid
import ui.components.street.StreetPetRefreshState
import ui.theme.backgroundColor


@Composable
fun StreetPetRoute(
    streetPetViewModel: StreetPetViewModel,
    returnToMainScreen: () -> Unit,
    openFilterSettings: () -> Unit,
    openStreetDetails: (String) -> Unit
) {
    val animals = streetPetViewModel.streetAnimals.collectAsLazyPagingItems()
    StreetPetScreen(
        streetPetScreenState = animals,
        returnToMainScreen = returnToMainScreen,
        openFilterSettings = openFilterSettings,
        openStreetDetails = openStreetDetails
    )
}

@Composable
fun StreetPetScreen(
    modifier: Modifier = Modifier,
    streetPetScreenState: LazyPagingItems<StreetPetPreviewModel>,
    returnToMainScreen: () -> Unit,
    openFilterSettings: () -> Unit,
    openStreetDetails: (String) -> Unit
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
            onBackClick = returnToMainScreen,
            onActionClick = openFilterSettings
        )
        Spacer(Modifier.height(10.dp))
        StreetPetSection(
            animals = streetPetScreenState,
            openStreetDetails = openStreetDetails,
        )
    }
}

@Composable
fun StreetPetSection(
    modifier: Modifier = Modifier,
    animals: LazyPagingItems<StreetPetPreviewModel>,
    openStreetDetails: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White)
    ) {
        StreetPetGrid(
            animals = animals,
            modifier = Modifier.fillMaxSize(),
            openStreetDetails = openStreetDetails
        )

        StreetPetRefreshState(
            refreshState = animals.loadState.refresh,
            isEmpty = animals.itemCount == 0,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
