package ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import domain.models.Creator
import domain.models.FoundPetInfo
import kotlinx.coroutines.launch
import ui.components.ButtonComponent
import ui.components.EventDateComponent
import ui.components.bottom_report.SeenPetBottomSheetContent
import ui.components.bottom_spotted.SpottedPetConfirmationBottomSheetContent
import ui.components.default_component.AnimatedToast
import ui.components.placeholder.SuccessSendPopup
import ui.theme.backgroundColor
import ui.theme.buttonPrimary
import ui.theme.buttonSecondPrimary
import ui.viewModel.FilterViewModel
import ui.viewModel.PetDetailsScreenState
import ui.viewModel.ReportFoundAnimalEffect
import ui.viewModel.ReportViewModel

private enum class DetailsPetBottomSheetType {
    FoundPetConfirmation,
    SeenPetLocation
}

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
    val scope = rememberCoroutineScope()

    var activeBottomSheet by remember {
        mutableStateOf<DetailsPetBottomSheetType?>(null)
    }

    var toastMessage by remember {
        mutableStateOf<String?>(null)
    }

    val spottedData by reportViewModel.spottedAnimalData.collectAsStateWithLifecycle()
    val reportUiState by reportViewModel.uiState.collectAsStateWithLifecycle()
    val foundPetState by viewModel.petInfoState.collectAsStateWithLifecycle()
    val findUriState by reportViewModel.findUriState.collectAsStateWithLifecycle()

    val isSendButtonEnabled =
        spottedData.uri.isNotEmpty() &&
                spottedData.lon != null &&
                spottedData.lat != null

    val pickSpottedImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let(reportViewModel::addImage)
    }

    val pickFoundImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let(reportViewModel::addFindImage)
    }

    LaunchedEffect(petId, announcementType) {
        viewModel.getInfoAboutPet(
            id = petId,
            announcementType = announcementType
        )
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
            activeBottomSheet = null
        }
    }

    LaunchedEffect(reportUiState.isSuccess) {
        if (reportUiState.isSuccess) {
            scaffoldState.bottomSheetState.partialExpand()
            activeBottomSheet = null
        }
    }

    LaunchedEffect(Unit) {
        reportViewModel.effect.collect { effect ->
            toastMessage = when (effect) {
                ReportFoundAnimalEffect.ServerError -> "Что-то пошло не так"
                ReportFoundAnimalEffect.InternetError -> "Проблемы с соединением"
            }
        }
    }
    val userState by viewModel.userState.collectAsState(null)

    fun closeBottomSheet() {
        scope.launch {
            scaffoldState.bottomSheetState.partialExpand()
            activeBottomSheet = null
        }
    }

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 1.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color(0xFFFAFAFA),
        sheetContentColor = Color(0xFF222222),
        sheetSwipeEnabled = activeBottomSheet != null,
        sheetContent = {
            when (activeBottomSheet) {
                DetailsPetBottomSheetType.FoundPetConfirmation -> {
                    SpottedPetConfirmationBottomSheetContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 400.dp, max = 760.dp),
                        photos = findUriState,
                        onAddPhotoClick = {
                            pickFoundImageLauncher.launch("image/*")
                        },
                        onSendClick = {
                            reportViewModel.reportFoundAnimal(petId)
                        }
                    )
                }

                DetailsPetBottomSheetType.SeenPetLocation -> {
                    SeenPetBottomSheetContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 400.dp, max = 760.dp),
                        photos = spottedData.uri,
                        buttonState = isSendButtonEnabled,
                        loadingState = reportUiState.isLoading,
                        updateLongitude = reportViewModel::updateLongitude,
                        updateLatitude = reportViewModel::updateLatitude,
                        onSendClick = {
                            reportViewModel.reportSpottedAnimal(id = petId)
                        },
                        onAddPhotoClick = {
                            pickSpottedImageLauncher.launch("image/*")
                        }
                    )
                }

                null -> {
                    Spacer(Modifier.height(1.dp))
                }
            }
        }
    ) { padding ->
        DetailPetScreen(
            modifier = Modifier.padding(padding),
            foundPetState = foundPetState,
            announcementType = announcementType,
            isMapSheetOpen = activeBottomSheet == DetailsPetBottomSheetType.SeenPetLocation,
            isSuccess = reportUiState.isSuccess,
            toastMessage = toastMessage,
            goBackClick = goBackClick,
            userState = userState?.id,
            onOwnerClick = onOwnerClick,
            onToastDismiss = {
                toastMessage = null
            },
            onFoundPetClick = {
                activeBottomSheet = DetailsPetBottomSheetType.FoundPetConfirmation
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            },
            onSeenPetClick = {
                activeBottomSheet = DetailsPetBottomSheetType.SeenPetLocation
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        )
    }
}

@Composable
fun DetailPetScreen(
    modifier: Modifier = Modifier,
    foundPetState: PetDetailsScreenState,
    announcementType: Int,
    isMapSheetOpen: Boolean,
    isSuccess: Boolean,
    toastMessage: String?,
    userState: String?,
    goBackClick: () -> Unit,
    onOwnerClick: (Creator) -> Unit,
    onToastDismiss: () -> Unit,
    onFoundPetClick: () -> Unit,
    onSeenPetClick: () -> Unit
) {
    when (foundPetState) {
        is PetDetailsScreenState.Failed -> {
            CircularProgressIndicator()
        }

        PetDetailsScreenState.Idle -> Unit

        PetDetailsScreenState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is PetDetailsScreenState.Success -> {
            DetailPetContent(
                modifier = modifier,
                petInfo = foundPetState.petInfo,
                announcementType = announcementType,
                isMapSheetOpen = isMapSheetOpen,
                isSuccess = isSuccess,
                userState = userState,
                toastMessage = toastMessage,
                goBackClick = goBackClick,
                onOwnerClick = onOwnerClick,
                onToastDismiss = onToastDismiss,
                onFoundPetClick = onFoundPetClick,
                onSeenPetClick = onSeenPetClick
            )
        }
    }
}

@Composable
private fun DetailPetContent(
    modifier: Modifier = Modifier,
    petInfo: FoundPetInfo,
    announcementType: Int,
    isMapSheetOpen: Boolean,
    isSuccess: Boolean,
    toastMessage: String?,
    userState: String?,
    goBackClick: () -> Unit,
    onOwnerClick: (Creator) -> Unit,
    onToastDismiss: () -> Unit,
    onFoundPetClick: () -> Unit,
    onSeenPetClick: () -> Unit
) {
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
                modifier = Modifier
                    .fillMaxHeight()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                PetInfoComponent(foundPetInfo = petInfo)

                Spacer(Modifier.height(32.dp))

                EventDateComponent(
                    advertState = "${petInfo.dateInfo.date} • ${petInfo.dateInfo.time}",
                    announcementType = announcementType
                )

                Spacer(Modifier.height(32.dp))

                WhereFindComponent(
                    foundPetInfo = petInfo,
                    announcementType = announcementType,
                    isMapSheetOpen = isMapSheetOpen,
                )

                Spacer(Modifier.height(32.dp))

                UserInfoComponent(
                    creatorInfo = petInfo.creator,
                    onClick = {
                        onOwnerClick(petInfo.creator)
                    }
                )

                Spacer(Modifier.height(20.dp))

                if (userState != petInfo.creator.id) {
                    PetActionButtons(
                        announcementType = announcementType,
                        onFoundPetClick = onFoundPetClick,
                        onSeenPetClick = onSeenPetClick
                    )
                }
            }
        }

        toastMessage?.let { message ->
            AnimatedToast(
                message = message,
                onDismiss = onToastDismiss
            )
        }

        SuccessSendPopup(
            visible = isSuccess,
            title = "Мы сообщили владельцу",
            description = "Спасибо за вашу отзывчивость!",
            onDismiss = goBackClick,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun PetActionButtons(
    announcementType: Int,
    onFoundPetClick: () -> Unit,
    onSeenPetClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                    radius = 40.dp,
                    onClick = {}
                )
            }

            1 -> {
                ButtonComponent(
                    modifier = Modifier.weight(1f),
                    color = buttonPrimary,
                    text = "Нашел питомца",
                    textColor = Color.White,
                    enabled = true,
                    radius = 40.dp,
                    onClick = onFoundPetClick
                )

                ButtonComponent(
                    modifier = Modifier.weight(1f),
                    color = buttonSecondPrimary,
                    text = "Видел питомца",
                    textColor = buttonPrimary,
                    enabled = true,
                    radius = 40.dp,
                    onClick = onSeenPetClick
                )
            }
        }
    }
}
