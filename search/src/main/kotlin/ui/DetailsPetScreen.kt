package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yandex.mapkit.geometry.Point
import domain.models.Creator
import domain.models.FoundPetInfo
import kotlinx.coroutines.launch
import ui.components.ButtonComponent
import ui.components.EventDateComponent
import ui.components.bottom_report.ReportAnnouncementBottomSheetContent
import ui.components.bottom_report.SeenPetBottomSheetContent
import ui.components.bottom_spotted.SpottedPetConfirmationBottomSheetContent
import ui.components.default_component.AnimatedToast
import ui.components.photo.rememberPhotoAttachmentPickerState
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.components.placeholder.ShimmerLoadingTransition
import ui.components.placeholder.ShimmerTextPlaceholder
import ui.components.placeholder.SuccessSendPopup
import ui.theme.backgroundColor
import ui.theme.buttonPrimary
import ui.theme.buttonSecondPrimary
import ui.viewModel.FilterViewModel
import ui.viewModel.PetDetailsScreenState
import ui.viewModel.ReportFoundAnimalEffect
import ui.viewModel.ReportViewModel
import ui.viewModel.ReportViewModel.Companion.REPORT_ANNOUNCEMENT_COMMENT_LIMIT

private enum class DetailsPetBottomSheetType {
    FoundPetConfirmation,
    SeenPetLocation,
    ReportAnnouncement
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
    val reportAnnouncementState by reportViewModel.reportAnnouncementState.collectAsStateWithLifecycle()
    val foundPetState by viewModel.petInfoState.collectAsStateWithLifecycle()
    val findUriState by reportViewModel.findUriState.collectAsStateWithLifecycle()
    val mapCameraLocation by reportViewModel.mapCameraLocation.collectAsStateWithLifecycle()
    val mapCameraPoint = mapCameraLocation?.let { location ->
        Point(location.latitude, location.longitude)
    }

    val isSendButtonEnabled =
        spottedData.uri.isNotEmpty() &&
                spottedData.lon != null &&
                spottedData.lat != null

    val foundPhotoPickerState = rememberPhotoAttachmentPickerState(
        selectedPhotoCount = findUriState.size,
        onPhotosSelected = reportViewModel::addFindImages
    )

    val spottedPhotoPickerState = rememberPhotoAttachmentPickerState(
        selectedPhotoCount = spottedData.uri.size,
        onPhotosSelected = reportViewModel::addImages
    )

    LaunchedEffect(petId, announcementType) {
        viewModel.getInfoAboutPet(
            id = petId,
            announcementType = announcementType
        )
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
            if (activeBottomSheet == DetailsPetBottomSheetType.ReportAnnouncement) {
                reportViewModel.closeReportAnnouncementSheet()
            }
            activeBottomSheet = null
        }
    }

    LaunchedEffect(reportUiState.isSuccess) {
        if (reportUiState.isSuccess) {
            scaffoldState.bottomSheetState.partialExpand()
            activeBottomSheet = null
        }
    }

    LaunchedEffect(reportAnnouncementState.isReportBottomSheetVisible) {
        if (!reportAnnouncementState.isReportBottomSheetVisible &&
            activeBottomSheet == DetailsPetBottomSheetType.ReportAnnouncement
        ) {
            scaffoldState.bottomSheetState.partialExpand()
            activeBottomSheet = null
        }
    }

    LaunchedEffect(Unit) {
        reportViewModel.effect.collect { effect ->
            toastMessage = when (effect) {
                ReportFoundAnimalEffect.ServerError -> "Что-то пошло не так"
                ReportFoundAnimalEffect.InternetError -> "Проблемы с соединением"
                is ReportFoundAnimalEffect.AnnouncementReportMessage -> effect.message
            }
        }
    }
    val userState by viewModel.userState.collectAsState(null)
    val isReportLoading = reportUiState.isLoading
    val isAnnouncementReportLoading = reportAnnouncementState.isReportLoading

    fun closeBottomSheet() {
        scope.launch {
            scaffoldState.bottomSheetState.partialExpand()
            if (activeBottomSheet == DetailsPetBottomSheetType.ReportAnnouncement) {
                reportViewModel.closeReportAnnouncementSheet()
            }
            activeBottomSheet = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = 1.dp,
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            sheetContainerColor = Color.White,
            sheetContentColor = Color(0xFF222222),
            sheetSwipeEnabled = activeBottomSheet != null &&
                    !isReportLoading &&
                    !isAnnouncementReportLoading,
            sheetContent = {
                when (activeBottomSheet) {
                    DetailsPetBottomSheetType.FoundPetConfirmation -> {
                        SpottedPetConfirmationBottomSheetContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 400.dp, max = 760.dp),
                            photos = findUriState,
                            isSendEnabled = findUriState.isNotEmpty() && !isReportLoading,
                            canAddPhoto = foundPhotoPickerState.canAddPhotos && !isReportLoading,
                            onAddPhotoClick = foundPhotoPickerState.openPicker,
                            onRemovePhotoClick = reportViewModel::removeFindImage,
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
                            buttonState = isSendButtonEnabled && !isReportLoading,
                            loadingState = false,
                            cameraLocation = mapCameraPoint,
                            updateLongitude = reportViewModel::updateLongitude,
                            updateLatitude = reportViewModel::updateLatitude,
                            onSendClick = {
                                reportViewModel.reportSpottedAnimal(id = petId)
                            },
                            canAddPhoto = spottedPhotoPickerState.canAddPhotos && !isReportLoading,
                            onAddPhotoClick = spottedPhotoPickerState.openPicker,
                            onRemovePhotoClick = reportViewModel::removeImage
                        )
                    }

                    DetailsPetBottomSheetType.ReportAnnouncement -> {
                        ReportAnnouncementBottomSheetContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 320.dp, max = 620.dp),
                            comment = reportAnnouncementState.reportComment,
                            isLoading = reportAnnouncementState.isReportLoading,
                            commentLimit = REPORT_ANNOUNCEMENT_COMMENT_LIMIT,
                            onCommentChange = reportViewModel::updateReportAnnouncementComment,
                            onSendClick = {
                                val ownerId =
                                    (foundPetState as? PetDetailsScreenState.Success)
                                        ?.petInfo
                                        ?.creator
                                        ?.id
                                        .orEmpty()
                                reportViewModel.reportAnnouncement(
                                    announcementId = petId,
                                    announcementOwnerId = ownerId,
                                    currentUserId = userState?.id
                                )
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
                isReportLoading = isReportLoading,
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
                },
                onReportAnnouncementClick = {
                    reportViewModel.openReportAnnouncementSheet()
                    activeBottomSheet = DetailsPetBottomSheetType.ReportAnnouncement
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            )
        }

        if (isReportLoading) {
            DetailsLoadingOverlay()
        }
    }
}

@Composable
private fun DetailsLoadingOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = buttonPrimary
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
    isReportLoading: Boolean,
    toastMessage: String?,
    userState: String?,
    goBackClick: () -> Unit,
    onOwnerClick: (Creator) -> Unit,
    onToastDismiss: () -> Unit,
    onFoundPetClick: () -> Unit,
    onSeenPetClick: () -> Unit,
    onReportAnnouncementClick: () -> Unit
) {
    ShimmerLoadingTransition(
        modifier = modifier.fillMaxSize(),
        isLoading = foundPetState is PetDetailsScreenState.Loading,
        loadingContent = {
            DetailPetShimmerPlaceholder(modifier = Modifier.fillMaxSize())
        }
    ) {
        when (foundPetState) {
            is PetDetailsScreenState.Failed -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            PetDetailsScreenState.Idle,
            PetDetailsScreenState.Loading -> Unit

            is PetDetailsScreenState.Success -> {
                DetailPetContent(
                    modifier = Modifier.fillMaxSize(),
                    petInfo = foundPetState.petInfo,
                    announcementType = announcementType,
                    isMapSheetOpen = isMapSheetOpen,
                    isSuccess = isSuccess,
                    isReportLoading = isReportLoading,
                    userState = userState,
                    toastMessage = toastMessage,
                    goBackClick = goBackClick,
                    onOwnerClick = onOwnerClick,
                    onToastDismiss = onToastDismiss,
                    onFoundPetClick = onFoundPetClick,
                    onSeenPetClick = onSeenPetClick,
                    onReportAnnouncementClick = onReportAnnouncementClick
                )
            }
        }
    }
}

@Composable
private fun DetailPetShimmerPlaceholder(
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
                .height(360.dp)
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(0.45f)
            )
            Spacer(Modifier.height(12.dp))
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.92f)
            )
            Spacer(Modifier.height(8.dp))
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.74f)
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(2) {
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.34f)
            )
            Spacer(Modifier.height(8.dp))
            ShimmerImagePlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(15.dp))
            )
            Spacer(Modifier.height(8.dp))
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.68f)
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerImagePlaceholder(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth(0.42f)
                    )
                    Spacer(Modifier.height(8.dp))
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth(0.6f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(2) {
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(40.dp))
                    )
                }
            }
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
    isReportLoading: Boolean,
    toastMessage: String?,
    userState: String?,
    goBackClick: () -> Unit,
    onOwnerClick: (Creator) -> Unit,
    onToastDismiss: () -> Unit,
    onFoundPetClick: () -> Unit,
    onSeenPetClick: () -> Unit,
    onReportAnnouncementClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var isMapTouched by remember { mutableStateOf(false) }
    val isOwnAnnouncement = userState == petInfo.creator.id

    LaunchedEffect(isMapSheetOpen) {
        if (isMapSheetOpen) {
            isMapTouched = false
        }
    }

    Box(
        modifier = modifier
            .background(color = backgroundColor)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.verticalScroll(
                state = scrollState,
                enabled = !isMapTouched
            )
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
                    onMapTouchStateChanged = { isMapTouched = it }
                )

                Spacer(Modifier.height(32.dp))

                UserInfoComponent(
                    creatorInfo = petInfo.creator,
                    onClick = {
                        onOwnerClick(petInfo.creator)
                    }
                )

                Spacer(Modifier.height(20.dp))

                if (!isOwnAnnouncement) {
                    PetActionButtons(
                        announcementType = announcementType,
                        enabled = !isReportLoading,
                        onFoundPetClick = onFoundPetClick,
                        onSeenPetClick = onSeenPetClick
                    )

                    Spacer(Modifier.height(12.dp))

                    ReportAnnouncementAction(
                        enabled = !isReportLoading,
                        onClick = onReportAnnouncementClick
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
private fun ReportAnnouncementAction(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0xFFFFE7E7))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Пожаловаться",
            color = Color(0xFFFF3B3B),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun PetActionButtons(
    announcementType: Int,
    enabled: Boolean,
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
                    enabled = enabled,
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
                    enabled = enabled,
                    radius = 40.dp,
                    onClick = onFoundPetClick
                )

                ButtonComponent(
                    modifier = Modifier.weight(1f),
                    color = buttonSecondPrimary,
                    text = "Видел питомца",
                    textColor = buttonPrimary,
                    enabled = enabled,
                    radius = 40.dp,
                    onClick = onSeenPetClick
                )
            }
        }
    }
}
