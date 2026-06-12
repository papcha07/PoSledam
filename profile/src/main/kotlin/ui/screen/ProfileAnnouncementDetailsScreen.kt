package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
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
import com.example.core.R
import domain.model.FoundReport
import domain.model.FoundReportContact
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import kotlinx.coroutines.launch
import ui.BASE_URL
import ui.components.SpottedLocationsMap
import ui.components.SpottedMapPoint
import ui.components.announcement.CancelAnnouncementReasonContent
import ui.components.default_component.AnimatedToast
import ui.components.placeholder.ErrorPlaceholder
import ui.components.placeholder.ShimmerAsyncImage
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.components.placeholder.ShimmerLoadingTransition
import ui.components.placeholder.ShimmerTextPlaceholder
import ui.components.profile.UnEditableContactComponent
import ui.model.AnnouncementCancelReason
import ui.theme.backgroundColor
import ui.theme.buttonPrimary
import ui.theme.eventDateComponentColor
import ui.theme.textHint
import ui.viewModel.ProfileAnnouncementDetailsState
import ui.viewModel.ProfileAnnouncementDetailsViewModel

private sealed interface ProfileDetailsBottomSheet {
    data object CancelAnnouncement : ProfileDetailsBottomSheet
    data class FoundReportDetails(val report: FoundReport) : ProfileDetailsBottomSheet
}

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

    val screenState by viewModel.detailsState.collectAsStateWithLifecycle()
    val cancelState by viewModel.cancelState.collectAsStateWithLifecycle()

    var selectedReasonId by remember { mutableStateOf(-1) }
    var activeBottomSheet by remember {
        mutableStateOf<ProfileDetailsBottomSheet?>(null)
    }

    LaunchedEffect(announcementId, announcementType) {
        viewModel.loadDetails(
            announcementId = announcementId,
            announcementType = announcementType
        )
    }

    LaunchedEffect(cancelState.isSuccess) {
        if (cancelState.isSuccess) {
            scaffoldState.bottomSheetState.partialExpand()
            activeBottomSheet = null
            viewModel.clearCancelResult()
            onBackClick()
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
            activeBottomSheet = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = 1.dp,
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            sheetContainerColor = Color(0xFFFAFAFA),
            sheetContentColor = Color(0xFF222222),
            sheetSwipeEnabled = activeBottomSheet != null,
            sheetContent = {
                when (val bottomSheet = activeBottomSheet) {
                    ProfileDetailsBottomSheet.CancelAnnouncement -> {
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
                                viewModel.cancelAnnouncement(
                                    reasonId,
                                    announcementType,
                                    announcementId
                                )
                            }
                        )
                    }

                    is ProfileDetailsBottomSheet.FoundReportDetails -> {
                        FoundReportDetailsBottomSheet(report = bottomSheet.report)
                    }

                    null -> Spacer(Modifier.height(1.dp))
                }
            }
        ) { paddingValues ->
            ProfileAnnouncementDetailsScreen(
                modifier = Modifier.padding(paddingValues),
                announcementId = announcementId,
                announcementType = announcementType,
                profileAnnouncementDetailsState = screenState,
                onBackClick = onBackClick,
                openBottom = {
                    activeBottomSheet = ProfileDetailsBottomSheet.CancelAnnouncement
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                },
                onFoundReportClick = { report ->
                    activeBottomSheet = ProfileDetailsBottomSheet.FoundReportDetails(report)
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            )
        }

        if (cancelState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = buttonPrimary
            )
        }

        cancelState.errorMessage?.let { message ->
            AnimatedToast(
                message = message,
                onDismiss = viewModel::clearCancelError
            )
        }
    }
}

@Composable
fun ProfileAnnouncementDetailsScreen(
    modifier: Modifier = Modifier,
    announcementId: String,
    announcementType: Int,
    profileAnnouncementDetailsState: ProfileAnnouncementDetailsState,
    onBackClick: () -> Unit,
    openBottom: () -> Unit,
    onFoundReportClick: (FoundReport) -> Unit
) {

    val state = profileAnnouncementDetailsState
    ShimmerLoadingTransition(
        modifier = modifier.fillMaxSize(),
        isLoading = state is ProfileAnnouncementDetailsState.Loading,
        loadingContent = {
            ProfileAnnouncementDetailsShimmerPlaceholder(
                modifier = Modifier.fillMaxSize(),
                announcementType = announcementType
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            when (state) {
                ProfileAnnouncementDetailsState.Idle,
                ProfileAnnouncementDetailsState.Loading -> Unit

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
                        foundReports = state.foundReports,
                        foundReportsError = state.foundReportsError,
                        onBackClick = onBackClick,
                        openBottom = openBottom,
                        onFoundReportClick = onFoundReportClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileAnnouncementDetailsShimmerPlaceholder(
    modifier: Modifier = Modifier,
    announcementType: Int
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
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(0.44f)
            )
            Spacer(Modifier.height(10.dp))
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

            Spacer(Modifier.height(18.dp))

            repeat(3) {
                ShimmerTextPlaceholder(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.56f)
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(18.dp))

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

            Spacer(Modifier.height(24.dp))

            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.22f)
            )
            Spacer(Modifier.height(8.dp))
            ShimmerTextPlaceholder(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.74f)
            )

            if (announcementType == MISSING_ANNOUNCEMENT_TYPE) {
                Spacer(Modifier.height(24.dp))
                ShimmerTextPlaceholder(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.38f)
                )
                Spacer(Modifier.height(12.dp))
                ShimmerImagePlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(15.dp))
                )
                Spacer(Modifier.height(24.dp))
                ShimmerTextPlaceholder(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.54f)
                )
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(2) {
                        ShimmerImagePlaceholder(
                            modifier = Modifier
                                .width(260.dp)
                                .height(210.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ProfileAnnouncementDetailsContent(
    announcement: ProfileAnnouncementDetails,
    announcementType: Int,
    spottedLocations: List<SpottedLocation>,
    spottedLocationsError: String?,
    foundReports: List<FoundReport>,
    foundReportsError: String?,
    onBackClick: () -> Unit,
    openBottom: () -> Unit,
    onFoundReportClick: (FoundReport) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            DetailsHeaderImage(
                imagePath = announcement.imagePath,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
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
                    Spacer(Modifier.height(24.dp))
                    FoundReportsBlock(
                        reports = foundReports,
                        errorMessage = foundReportsError,
                        onReportClick = onFoundReportClick
                    )
                }

                Spacer(Modifier.height(100.dp))
            }
        }

        DeleteButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            openBottom = openBottom
        )
    }
}

@Composable
private fun DetailsHeaderImage(
    imagePath: String?,
    onBackClick: () -> Unit
) {
    Box {
        ShimmerAsyncImage(
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
private fun FoundReportsBlock(
    reports: List<FoundReport>,
    errorMessage: String?,
    onReportClick: (FoundReport) -> Unit
) {
    Column {
        Text(
            text = "Сообщения о находке",
            fontSize = 18.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Откройте сообщение, чтобы посмотреть фотографии и данные человека",
            fontSize = 13.sp,
            color = textHint
        )
        Spacer(Modifier.height(12.dp))

        when {
            errorMessage != null -> RouteMessage(text = errorMessage)
            reports.isEmpty() -> RouteMessage(text = "Питомца пока никто не находил")
            else -> LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = reports,
                    key = { it.id }
                ) { report ->
                    FoundReportCard(
                        report = report,
                        onClick = { onReportClick(report) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FoundReportCard(
    report: FoundReport,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            ShimmerAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(12.dp)),
                model = report.imagesPath.firstOrNull()?.toImageModel(),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = report.user.name,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = report.createdAtText(),
                fontSize = 13.sp,
                color = textHint
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (report.imagesPath.isEmpty()) {
                    "Фото не приложены"
                } else {
                    "Фото: ${report.imagesPath.size}"
                },
                fontSize = 13.sp,
                color = buttonPrimary
            )
        }
    }
}

@Composable
private fun FoundReportDetailsBottomSheet(
    report: FoundReport
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Text(
            text = "Питомца нашли",
            fontSize = 22.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Сообщил: ${report.user.name}",
            fontSize = 14.sp,
            color = textHint
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = report.createdAtText(),
            fontSize = 14.sp,
            color = textHint
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Фотографии",
            fontSize = 18.sp
        )
        Spacer(Modifier.height(10.dp))
        if (report.imagesPath.isEmpty()) {
            RouteMessage(text = "Фотографии не приложены")
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(
                    items = report.imagesPath,
                    key = { index, imagePath -> "$index-$imagePath" }
                ) { _, imagePath ->
                    ShimmerAsyncImage(
                        modifier = Modifier
                            .width(220.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        model = imagePath.toImageModel(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Как связаться",
            fontSize = 18.sp
        )
        Spacer(Modifier.height(10.dp))
        if (report.user.contacts.isEmpty()) {
            RouteMessage(text = "Сервер не передал контакты пользователя")
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                report.user.contacts.forEach { contact ->
                    UnEditableContactComponent(
                        uri = contact.url,
                        icon = contact.iconRes()
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
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

private fun FoundReport.createdAtText(): String {
    return listOf(createdDate, createdTime)
        .filter { it.isNotBlank() }
        .joinToString(separator = " • ")
        .ifBlank { "Дата не указана" }
}

private fun FoundReportContact.iconRes(): Int {
    return when (type) {
        VK_CONTACT_TYPE -> R.drawable.ic_vk
        TG_CONTACT_TYPE -> R.drawable.ic_tg
        WHATSAPP_CONTACT_TYPE -> R.drawable.ic_whatsapp
        else -> R.drawable.copy
    }
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
    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
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
}

private const val MISSING_ANNOUNCEMENT_TYPE = 0
private const val VK_CONTACT_TYPE = 0
private const val TG_CONTACT_TYPE = 1
private const val WHATSAPP_CONTACT_TYPE = 2
