package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import domain.models.Creator
import ui.components.ButtonComponent
import ui.components.EventDateComponent
import ui.components.default_component.AnimatedToast
import ui.components.placeholder.SuccessSendPopup
import ui.theme.backgroundColor
import ui.theme.buttonPrimary
import ui.theme.buttonSecondPrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPetScreenProvider(
    modifier: Modifier = Modifier,
    viewModel: FilterViewModel,
    reportViewModel: ReportViewModel,
    petId: String,
    announcementType: Int,
    goBackClick: () -> Unit,
    onOwnerClick: (Creator) -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetSwipeEnabled = true,
        sheetContent = {

        }
    ) { padding ->
        DetailPetScreen(
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
            reportViewModel = reportViewModel,
            petId = petId,
            announcementType = announcementType,
            goBackClick = goBackClick,
            onOwnerClick = onOwnerClick
        )
    }
}

@Composable
fun DetailPetScreen(
    modifier: Modifier = Modifier,
    viewModel: FilterViewModel,
    reportViewModel: ReportViewModel,
    petId: String,
    announcementType: Int,
    goBackClick: () -> Unit,
    onOwnerClick: (Creator) -> Unit
) {
    LaunchedEffect(Unit) { viewModel.getInfoAboutPet(petId) }

    val foundPetState by viewModel.petInfoState.collectAsState()
    when (foundPetState) {

        is PetDetailsScreenState.Failed -> {
            CircularProgressIndicator()
        }

        PetDetailsScreenState.Idle -> {}

        PetDetailsScreenState.Loading -> {
            Box {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        is PetDetailsScreenState.Success -> {
            val petInfo = (foundPetState as PetDetailsScreenState.Success).petInfo
            val uiState by reportViewModel.uiState.collectAsStateWithLifecycle()
            var toastMessage by remember {
                mutableStateOf<String?>(null)
            }

            Box(
                modifier = modifier
                    .background(color = backgroundColor)
                    .fillMaxSize()
            ) {

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    PetImageComponent(
                        goBackClick = goBackClick,
                        foundPetInfo = petInfo
                    )
                    Column(
                        modifier = modifier
                            .fillMaxHeight()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        PetInfoComponent(foundPetInfo = petInfo)
                        Spacer(Modifier.height(32.dp))
                        EventDateComponent(advertState = "${petInfo.dateInfo.date} • ${petInfo.dateInfo.time}")
                        Spacer(Modifier.height(32.dp))
                        WhereFindComponent(foundPetInfo = petInfo)
                        Spacer(Modifier.height(32.dp))
                        UserInfoComponent(
                            creatorInfo = petInfo.creator,
                            onClick = { onOwnerClick(petInfo.creator) }
                        )
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (announcementType) {
                                0 -> {
                                    ButtonComponent(
                                        modifier = Modifier.weight(1f),
                                        color = buttonPrimary,
                                        text = "Это мое животное",
                                        textColor = Color.White,
                                        enabled = true,
                                        radius = 40.dp
                                    ) {

                                    }
                                }

                                1 -> {
                                    ButtonComponent(
                                        modifier = Modifier.weight(1f),
                                        color = buttonPrimary,
                                        text = "Нашел питомца",
                                        textColor = Color.White,
                                        enabled = true,
                                        radius = 40.dp,
                                        onClick = {
                                            reportViewModel.reportFoundAnimal(petId)
                                        },
                                    )

                                    ButtonComponent(
                                        modifier = Modifier.weight(1f),
                                        color = buttonSecondPrimary,
                                        text = "Видел питомца",
                                        textColor = buttonPrimary,
                                        enabled = true,
                                        radius = 40.dp
                                    ) {

                                    }
                                }
                            }
                        }
                    }
                }

                var toastMessage by remember {
                    mutableStateOf<String?>(null)
                }

                LaunchedEffect(Unit) {
                    reportViewModel.effect.collect { effect ->
                        toastMessage = when (effect) {
                            ReportFoundAnimalEffect.ServerError -> "Что-то пошло не так"
                            ReportFoundAnimalEffect.InternetError -> "Проблемы с соединением"
                        }
                    }
                }

                toastMessage?.let {
                    AnimatedToast(
                        message = it,
                        onDismiss = {
                            toastMessage = null
                        }
                    )
                }

                SuccessSendPopup(
                    visible = uiState.isSuccess,
                    title = "Мы сообщили владельцу",
                    description = "Спасибо за вашу отзывчивость!",
                    onDismiss = goBackClick,
                    modifier = Modifier.align(Alignment.Center)
                )

            }
        }
    }

}








