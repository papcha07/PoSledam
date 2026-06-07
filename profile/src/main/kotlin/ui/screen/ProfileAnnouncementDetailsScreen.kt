package ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.core.R
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import kotlinx.coroutines.launch
import ui.BASE_URL
import ui.components.SpottedLocationsMap
import ui.components.SpottedMapPoint
import ui.components.announcement.CancelAnnouncementReasonContent
import ui.components.placeholder.ErrorPlaceholder
import ui.model.AnnouncementCancelReason
import ui.theme.backgroundColor
import ui.theme.buttonPrimary
import ui.theme.eventDateComponentColor
import ui.theme.textHint
import ui.viewModel.ProfileAnnouncementDetailsState
import ui.viewModel.ProfileAnnouncementDetailsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAnnouncementDetailsProvider(
    modifier: Modifier = Modifier,
    announcementId: String,
    announcementType: Int,
    viewModel: ProfileAnnouncementDetailsViewModel,
    onBackClick: () -> Unit
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    var isMapSheetOpen by remember {
        mutableStateOf(false)
    }

    val screenState by viewModel.detailsState.collectAsStateWithLifecycle()

    LaunchedEffect(announcementId, announcementType) {
        viewModel.loadDetails(
            announcementId = announcementId,
            announcementType = announcementType
        )
    }
    var selectedReasonId by remember { mutableStateOf(-1) }

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 1.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color(0xFFFAFAFA),
        sheetContentColor = Color(0xFF222222),
        sheetSwipeEnabled = true,
        sheetContent = {
            val cancelReasons = if (announcementType == MISSING_ANNOUNCEMENT_TYPE) {
                AnnouncementCancelReason.missingAnnouncementOptions
            } else {
                AnnouncementCancelReason.foundAnnouncementOptions
            }

            CancelAnnouncementReasonContent(
                selectedReasonId = selectedReasonId,
                reasons = cancelReasons,
                onReasonSelected = { reasonId ->
                    selectedReasonId = reasonId
                },
                onCancelAnnouncement = { reasonId ->
                    viewModel.cancelAnnouncement(reasonId, announcementType, announcementId)
                }
            )
        }
    ) { paddingValues ->
        ProfileAnnouncementDetailsScreen(
            modifier = Modifier.padding(paddingValues),
            announcementId = announcementId,
            announcementType = announcementType,
            profileAnnouncementDetailsState = screenState,
            onBackClick = onBackClick,
            openBottom = {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        )
    }
}

@Composable
fun ProfileAnnouncementDetailsScreen(
    modifier: Modifier = Modifier,
    announcementId: String,
    announcementType: Int,
    profileAnnouncementDetailsState: ProfileAnnouncementDetailsState,
    onBackClick: () -> Unit,
    openBottom: () -> Unit
) {

    val state = profileAnnouncementDetailsState
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        when (state) {
            ProfileAnnouncementDetailsState.Idle,
            ProfileAnnouncementDetailsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = buttonPrimary
                )
            }

            is ProfileAnnouncementDetailsState.Failed -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ErrorPlaceholder()
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        color = textHint,
                        fontSize = 14.sp
                    )
                }
            }

            is ProfileAnnouncementDetailsState.Success -> {
                ProfileAnnouncementDetailsContent(
                    announcement = state.announcement,
                    announcementType = announcementType,
                    spottedLocations = state.spottedLocations,
                    spottedLocationsError = state.spottedLocationsError,
                    onBackClick = onBackClick,
                    openBottom = openBottom
                )
            }
        }
    }
}

@Composable
private fun ProfileAnnouncementDetailsContent(
    announcement: ProfileAnnouncementDetails,
    announcementType: Int,
    spottedLocations: List<SpottedLocation>,
    spottedLocationsError: String?,
    onBackClick: () -> Unit,
    openBottom: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        DetailsHeaderImage(
            imagePath = announcement.imagePath,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            PetParametersBlock(announcement = announcement)

            Spacer(Modifier.height(24.dp))

            DetailsDateBlock(
                title = if (announcementType == MISSING_ANNOUNCEMENT_TYPE) {
                    "Когда потеряли"
                } else {
                    "Когда нашли"
                },
                value = "${announcement.eventDate} • ${announcement.eventTime}"
            )

            Spacer(Modifier.height(24.dp))

            LocationBlock(announcement = announcement)

            if (announcementType == MISSING_ANNOUNCEMENT_TYPE) {
                Spacer(Modifier.height(24.dp))
                SpottedRouteBlock(
                    spottedLocations = spottedLocations,
                    errorMessage = spottedLocationsError
                )
            }

            DeleteButton(
                openBottom = openBottom
            )
        }
    }
}

@Composable
private fun DetailsHeaderImage(
    imagePath: String?,
    onBackClick: () -> Unit
) {
    Box {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                ),
            model = imagePath?.toImageModel(),
            placeholder = painterResource(R.drawable.ic_dog),
            error = painterResource(R.drawable.ic_dog),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Image(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 60.dp)
                .clickable { onBackClick() },
            painter = painterResource(R.drawable.ic_back_found),
            contentDescription = "Назад"
        )
    }
}

@Composable
private fun PetParametersBlock(
    announcement: ProfileAnnouncementDetails
) {
    Column {
        Text(
            text = announcement.breed.ifBlank { "Порода не указана" },
            fontSize = 22.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = announcement.description.ifBlank { "Описание не указано" },
            fontSize = 14.sp,
            color = textHint
        )
        Spacer(Modifier.height(16.dp))
        ParameterText(
            title = "Тип",
            value = announcement.petType.toPetTypeText()
        )
        Spacer(Modifier.height(8.dp))
        ParameterText(
            title = "Пол",
            value = announcement.gender.toGenderText()
        )
        Spacer(Modifier.height(8.dp))
        ParameterText(
            title = "Окрас",
            value = announcement.color.ifBlank { "Не указан" }
        )
    }
}

@Composable
private fun ParameterText(
    title: String,
    value: String
) {
    Row {
        Text(
            text = "$title:",
            color = textHint,
            fontSize = 14.sp
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = value,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DetailsDateBlock(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(eventDateComponentColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(38.dp),
            painter = painterResource(R.drawable.ic_calendar_component),
            contentDescription = null
        )
        Column(
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(
                text = title,
                color = textHint,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun LocationBlock(
    announcement: ProfileAnnouncementDetails
) {
    Column {
        Text(
            text = "Место",
            fontSize = 18.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = announcement.addressText(),
            fontSize = 14.sp,
            color = textHint
        )
    }
}

@Composable
private fun SpottedRouteBlock(
    spottedLocations: List<SpottedLocation>,
    errorMessage: String?
) {
    Column {
        Text(
            text = "След питомца",
            fontSize = 18.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Нажмите на лапку, чтобы посмотреть отметку и фотографии",
            fontSize = 13.sp,
            color = textHint
        )
        Spacer(Modifier.height(12.dp))

        when {
            errorMessage != null -> RouteMessage(text = errorMessage)
            spottedLocations.isEmpty() -> RouteMessage(text = "Питомца пока никто не отмечал")
            else -> SpottedLocationsMap(
                modifier = Modifier
                    .height(280.dp)
                    .clip(RoundedCornerShape(15.dp)),
                points = spottedLocations.map { it.toMapPoint() }
            )
        }
    }
}

@Composable
private fun RouteMessage(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(backgroundColor, RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text,
            color = textHint,
            fontSize = 14.sp
        )
    }
}

private fun ProfileAnnouncementDetails.addressText(): String {
    val streetAndHouse = listOfNotNull(street, house)
        .filter { it.isNotBlank() }
        .joinToString(separator = " ")
    return listOfNotNull(district, streetAndHouse.ifBlank { null })
        .filter { it.isNotBlank() }
        .joinToString(separator = ", ")
        .ifBlank { "Адрес не указан" }
}

private fun SpottedLocation.toMapPoint(): SpottedMapPoint {
    return SpottedMapPoint(
        id = id,
        latitude = latitude,
        longitude = longitude,
        spottedBy = "Отметил: $spottedUserName",
        createdAt = listOf(createdDate, createdTime)
            .filter { it.isNotBlank() }
            .joinToString(separator = " • "),
        imagePaths = imagesPath
    )
}

private fun Int.toPetTypeText(): String {
    return when (this) {
        0 -> "Кот"
        1 -> "Собака"
        else -> "Другое"
    }
}

private fun Int.toGenderText(): String {
    return when (this) {
        0 -> "Мальчик"
        1 -> "Девочка"
        else -> "Неизвестно"
    }
}

private fun String.toImageModel(): String {
    return when {
        startsWith("http://") || startsWith("https://") || startsWith("content://") -> this
        else -> "$BASE_URL/api/image/${trimStart('/')}"
    }
}

@Composable
fun DeleteButton(
    modifier: Modifier = Modifier,
    openBottom: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        onClick = openBottom,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFE7E7),
            contentColor = Color(0xFFFF3B3B),
            disabledContainerColor = Color(0xFFFFE7E7).copy(alpha = 0.5f),
            disabledContentColor = Color(0xFFFF3B3B).copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = "Снять с публикации",
            fontSize = 16.sp
        )
    }
}

private const val MISSING_ANNOUNCEMENT_TYPE = 0
