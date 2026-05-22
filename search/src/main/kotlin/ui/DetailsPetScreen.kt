package ui

import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.launch
import ui.components.ButtonComponent
import ui.components.EventDateComponent
import ui.components.bottom_report.SeenPetBottomSheetContent
import ui.components.default_component.AnimatedToast
import ui.components.placeholder.SuccessSendPopup
import ui.theme.backgroundColor
import ui.theme.buttonPrimary
import ui.theme.buttonSecondPrimary
import ui.viewModel.FilterViewModel
import ui.viewModel.PetDetailsScreenState
import ui.viewModel.ReportFoundAnimalEffect
import ui.viewModel.ReportViewModel


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
    var isMapSheetOpen by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
            isMapSheetOpen = false
        }
    }

    val sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val spottedData by reportViewModel.spottedAnimalData.collectAsStateWithLifecycle()
    val allFilled =
        spottedData.uri.isNotEmpty() && spottedData.lon != null && spottedData.lat != null
    val pickImageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                reportViewModel.addImage(it)
            }
        }
    val uiState by reportViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            scaffoldState.bottomSheetState.partialExpand()
            isMapSheetOpen = false
        }
    }



    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 1.dp,
        sheetShape = sheetShape,
        sheetContainerColor = Color(0xFFFAFAFA),
        sheetContentColor = Color(0xFF222222),
        sheetSwipeEnabled = true,
        sheetContent = {
            SeenPetBottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 400.dp, max = 760.dp),
                photos = spottedData.uri,
                buttonState = allFilled,
                loadingState = uiState.isLoading,
                updateLongitude = reportViewModel::updateLongitude,
                updateLatitude = reportViewModel::updateLatitude,
                onSendClick = {
                    reportViewModel.reportSpottedAnimal(id = petId)
                },
                onAddPhotoClick = {
                    pickImageLauncher.launch("image/*")
                }
            )
        }
    ) { padding ->
        DetailPetScreen(
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
            reportViewModel = reportViewModel,
            petId = petId,
            announcementType = announcementType,
            goBackClick = goBackClick,
            onOwnerClick = onOwnerClick,
            isMapSheetOpen = isMapSheetOpen,
            openBottomMenu = {
                scope.launch {
                    isMapSheetOpen = true
                    scaffoldState.bottomSheetState.expand()
                }
            }
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
    onOwnerClick: (Creator) -> Unit,
    openBottomMenu: () -> Unit,
    isMapSheetOpen: Boolean
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
                        WhereFindComponent(
                            foundPetInfo = petInfo,
                            isMapSheetOpen = isMapSheetOpen
                        )
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
                                        radius = 40.dp,
                                        onClick = {
                                            Log.d("BOTTOM_SHEET", "button clicked")

                                            openBottomMenu()
                                        }
                                    )
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








