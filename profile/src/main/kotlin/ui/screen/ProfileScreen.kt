package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ui.components.AnimalCard
import ui.components.PetButton
import ui.components.default_component.TabRowSelection
import ui.components.placeholder.EmptyAnimalList
import ui.components.placeholder.ErrorPlaceholder
import ui.components.profilebar.ProfileBarComponent
import ui.model.TabRowInfo
import ui.theme.backgroundColor
import ui.viewModel.ProfileScreenState
import ui.viewModel.ProfileSettingsViewModel
import ui.viewModel.ProfileViewModel


@Composable
fun ProfileScreen(
    navigateToActionScreen: () -> Unit,
    openProfileSettings: () -> Unit,
    profileViewModel: ProfileViewModel,
    profileSettingsViewModel: ProfileSettingsViewModel,
) {


    LaunchedEffect(Unit) {
        profileSettingsViewModel.observeUser()
        profileViewModel.getAnimalList()
    }

    val userInfoState by profileSettingsViewModel.userInfoState.collectAsStateWithLifecycle()
    val methodIndex by profileViewModel.userMethodState.collectAsState()
    val animalListState by profileViewModel.userPetState.collectAsState()

    Column(modifier = Modifier.background(backgroundColor)) {
        ProfileBarComponent(
            profileBarState = userInfoState,
            onSettingsClick = openProfileSettings,
            notificationsIsNotRead = false,
            onNotifyClick = {

            }
        )
        MainContentComponent(
            selectedIndex = methodIndex,
            navigateToActionScreen = navigateToActionScreen,
            updateMethodIndex = profileViewModel::updateMethodValue,
            animalListState = animalListState,
            loadAnimalList = profileViewModel::getAnimalList,
        )
    }
}


@Composable
fun MainContentComponent(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    animalListState: ProfileScreenState,
    navigateToActionScreen: () -> Unit,
    updateMethodIndex: (Int) -> Unit,
    loadAnimalList: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            TabRowSelection(
                modifier = Modifier
                    .padding(top = 16.dp),
                listOfTabInfo = listOf(
                    TabRowInfo("Пропажи"),
                    TabRowInfo("Найденные")
                ),
                selectedTabIndex = selectedIndex,
                onTabSelected = { index ->
                    updateMethodIndex(index)
                    loadAnimalList()
                }
            )
            Spacer(Modifier.height(24.dp))
            when (selectedIndex) {
                1 -> PetLazyRow(
                    profileScreenState = animalListState,
                    animalType = selectedIndex,
                )

                0 -> PetLazyRow(
                    profileScreenState = animalListState,
                    animalType = selectedIndex,
                )
            }

        }
        PetButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
                .align(Alignment.BottomCenter),
            onClick = {
                navigateToActionScreen()
            },
        )
    }


}

@Composable
fun PetLazyRow(
    modifier: Modifier = Modifier,
    profileScreenState: ProfileScreenState,
    animalType: Int,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .testTag("animalType_$animalType")
    ) {
        when (profileScreenState) {
            ProfileScreenState.Empty -> {
                EmptyAnimalList(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("empty_list")
                        .align(Alignment.Center)
                )
            }

            ProfileScreenState.Failed -> {
                ErrorPlaceholder(modifier = Modifier.align(Alignment.Center))
            }

            ProfileScreenState.Idle -> {

            }

            ProfileScreenState.Loading -> {
                CircularProgressIndicator(
                    Modifier
                        .align(Alignment.Center)
                        .testTag("progress_bar")
                )
            }

            is ProfileScreenState.Success -> {
                LazyColumn(
                    modifier = modifier
                        .padding(horizontal = 16.dp)
                        .testTag("animal_list")
                ) {
                    items(profileScreenState.petList) { petInfo ->
                        AnimalCard(
                            modifier = modifier,
                            petInfo = petInfo,
                            currentState = animalType,
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }

            }
        }
    }
}





