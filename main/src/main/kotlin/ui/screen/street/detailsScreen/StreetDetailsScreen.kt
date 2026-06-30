package ui.screen.street.detailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yandex.mapkit.geometry.Point
import domain.models.StreetDetails
import ui.components.BackCircleButton
import ui.components.CurrentLocationMap
import ui.components.EventDateComponent
import ui.components.default_component.AnimatedToast
import ui.components.placeholder.ErrorPlaceholder
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.components.placeholder.ShimmerLoadingTransition
import ui.components.placeholder.ShimmerTextPlaceholder
import ui.components.streetPager.StreetPhotoPager
import ui.model.ScreenState
import ui.screen.street.StreetReportEffect
import ui.screen.street.StreetReportUiState
import ui.screen.street.StreetPetViewModel
import ui.screen.street.StreetPetViewModel.Companion.REPORT_COMMENT_LIMIT
import ui.screen.street.detailsScreen.component.DescriptionComponent
import ui.theme.Ser
import ui.theme.backgroundColor
import ui.theme.buttonPrimary
import ui.theme.eventDateComponentColor


@OptIn(ExperimentalMaterial3Api::class)
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
    val reportUiState by streetPetViewModel.reportUiState.collectAsStateWithLifecycle()
    val currentUser by streetPetViewModel.userState.collectAsStateWithLifecycle(null)
    var toastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        streetPetViewModel.reportEffect.collect { effect ->
            toastMessage = when (effect) {
                is StreetReportEffect.Message -> effect.message
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        StreetDetailsScreen(
            detailsState = detailsState,
            currentUserId = currentUser?.id,
            returnBack = returnBack,
            onReportClick = streetPetViewModel::openReportBottomSheet
        )

        toastMessage?.let { message ->
            AnimatedToast(
                message = message,
                onDismiss = {
                    toastMessage = null
                }
            )
        }
    }

    if (reportUiState.isReportBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                if (!reportUiState.isReportLoading) {
                    streetPetViewModel.closeReportBottomSheet()
                }
            },
            containerColor = Color.White,
            dragHandle = null
        ) {
            val ownerId = (detailsState as? ScreenState.Success<StreetDetails>)
                ?.data
                ?.creator
                ?.id
                .orEmpty()
            StreetReportBottomSheetContent(
                reportUiState = reportUiState,
                onCommentChange = streetPetViewModel::updateReportComment,
                onSendClick = {
                    streetPetViewModel.reportAnnouncement(
                        announcementId = animalId,
                        announcementOwnerId = ownerId,
                        currentUserId = currentUser?.id
                    )
                }
            )
        }
    }
}

@Composable
fun StreetDetailsScreen(
    modifier: Modifier = Modifier,
    detailsState: ScreenState<StreetDetails>,
    currentUserId: String?,
    returnBack: () -> Unit,
    onReportClick: () -> Unit
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
                                showReportAction = currentUserId != detailsState.data.creator.id,
                                onReportClick = onReportClick
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
    showReportAction: Boolean = false,
    onReportClick: () -> Unit = {}
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

        if (showReportAction) {
            Spacer(Modifier.height(24.dp))
            StreetReportAction(onClick = onReportClick)
        }
    }
}

@Composable
private fun StreetReportAction(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0xFFFFE7E7))
            .clickable(onClick = onClick),
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
private fun StreetReportBottomSheetContent(
    modifier: Modifier = Modifier,
    reportUiState: StreetReportUiState,
    onCommentChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val trimmedComment = reportUiState.reportComment.trim()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Пожаловаться",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = Color(0xFF1E1E1E)
        )

        Spacer(Modifier.height(22.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
                .clip(RoundedCornerShape(14.dp)),
            value = reportUiState.reportComment,
            onValueChange = { value ->
                onCommentChange(value.take(REPORT_COMMENT_LIMIT))
            },
            enabled = !reportUiState.isReportLoading,
            placeholder = {
                Text(
                    text = "Опишите причину жалобы",
                    color = Color(0xFF8A8A8A),
                    fontSize = 15.sp
                )
            },
            textStyle = TextStyle(
                color = Color(0xFF1E1E1E),
                fontSize = 15.sp,
                lineHeight = 21.sp
            ),
            minLines = 5,
            maxLines = 7,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE7E7E7),
                unfocusedContainerColor = Color(0xFFE7E7E7),
                disabledContainerColor = Color(0xFFE7E7E7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = buttonPrimary
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${reportUiState.reportComment.length}/$REPORT_COMMENT_LIMIT",
            textAlign = TextAlign.End,
            fontSize = 12.sp,
            color = Ser
        )

        Spacer(Modifier.height(22.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = trimmedComment.isNotEmpty() && !reportUiState.isReportLoading,
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonPrimary,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color.White
            ),
            onClick = onSendClick
        ) {
            if (reportUiState.isReportLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Отправить жалобу",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
