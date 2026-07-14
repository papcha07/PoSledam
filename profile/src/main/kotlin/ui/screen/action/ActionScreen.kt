package ui.screen.action

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.core.R
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import com.yandex.mapkit.geometry.Point
import ui.components.ProfileMap
import ui.components.TabRowMethodSelection
import ui.components.default_component.PetTextField
import ui.components.placeholder.SuccessSendPopup
import ui.components.slider.PhotosPager
import ui.model.ActionScreenState
import ui.model.TabRowInfo
import ui.screen.PlaceAnnouncementComponent
import ui.theme.BrushColor
import ui.theme.PurpleButtonColor
import ui.theme.Ser
import ui.theme.backgroundColor
import ui.theme.blueStatusColorButton
import ui.theme.buttonPrimary
import ui.theme.deleteButtonColor
import ui.theme.greyStatusColorButton
import ui.theme.purpleStatusColor
import ui.theme.textHint
import ui.viewModel.ActionPage
import ui.viewModel.ActionScreenData
import ui.viewModel.ActionViewModel
import java.time.LocalDate
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActionScreen(
    modifier: Modifier = Modifier,
    onProfilePage: () -> Unit,
    viewModel: ActionViewModel
) {
    val announcementInfo by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val page by viewModel.pageState.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val handleBack = {
        if (!viewModel.goToPreviousPage()) {
            onProfilePage()
        }
    }

    BackHandler(onBack = handleBack)

    Box(
        Modifier
            .background(color = backgroundColor)
            .fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(5.dp))
            ActionTopBar(
                viewModel = viewModel,
                page = page,
                onBack = handleBack,
            )
            when (page) {
                ActionPage.MAIN -> ActionMainComponent(
                    actionViewModel = viewModel,
                    announcementInfo = announcementInfo
                )

                ActionPage.ADDRESS -> AddressMainComponent(
                    actionViewModel = viewModel,
                    actionScreenData = announcementInfo,
                    enableScroll = false
                )

                ActionPage.RESULT -> PlaceAnnouncementComponent(actionViewModel = viewModel)
            }
        }

        if (uiState is ActionScreenState.Loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.45f))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("progress_bar"))
            }
        }

        if (uiState is ActionScreenState.SuccessAction) {
            if (!notificationsEnabled) {
                Dialog(
                    onDismissRequest = { viewModel.dismissSuccess() },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false,
                        decorFitsSystemWindows = false
                    )
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(0.45f))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SuccessSendPopup(
                            visible = true,
                            title = "Объявление подано",
                            description = "Включите уведомления чтобы не пропустить\nуведомления",
                            onDismiss = {
                                viewModel.enableNotifications()
                                viewModel.dismissSuccess()
                                onProfilePage()
                            },
                        )
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    viewModel.dismissSuccess()
                    onProfilePage()
                }
            }
        }
    }
}


@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
    pageState: ActionPage
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(buttonPrimary)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        when (pageState) {
                            ActionPage.MAIN -> greyStatusColorButton
                            ActionPage.ADDRESS -> buttonPrimary
                            ActionPage.RESULT -> deleteButtonColor
                        }
                    )
            )
        }


    }
}

@Composable
fun ActionTopBar(
    modifier: Modifier = Modifier,
    viewModel: ActionViewModel,
    page: ActionPage,
    onBack: () -> Unit,
) {
    val methodFlowValue by viewModel.methodValueFlow.collectAsState()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.testTag("back_button"),
                    onClick = onBack
                ) {
                    Icon(
                        painter = painterResource(R.drawable.left_arrow),
                        contentDescription = "Назад"
                    )
                }

                Text(
                    text = "Объявление",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
            ProgressIndicator(
                Modifier
                    .padding(top = 8.dp),
                pageState = page
            )
            when (page) {
                ActionPage.MAIN -> {
                    Spacer(Modifier.height(18.dp))
                    TabRowMethodSelection(
                        listOfTabInfo = listOf(
                            TabRowInfo("Потерялся"),
                            TabRowInfo("Нашелся")
                        ),
                        selectedTabIndex = methodFlowValue,
                        onTabSelected = viewModel::updateMethodValue,
                    )
                }

                else -> {

                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddressMainComponent(
    modifier: Modifier = Modifier,
    actionScreenData: ActionScreenData,
    actionViewModel: ActionViewModel,
    enableScroll: Boolean = true
) {
    val calendarState = rememberUseCaseState()
    val clockState = rememberUseCaseState()
    val addressFillState by actionViewModel.isAddressComponentState.collectAsState()
    val mapCameraLocation = actionScreenData.mapCameraLocation?.let { location ->
        Point(location.latitude, location.longitude)
    }
    val scrollState = rememberScrollState()

    LaunchedEffect(actionViewModel) {
        actionViewModel.setCurrentLocation()
    }

    CalendarDialog(
        state = calendarState,
        selection = CalendarSelection.Date { date ->
            actionViewModel.updateSelectedDate(date)
        },
        config = CalendarConfig(
            boundary = LocalDate.MIN..LocalDate.now()
        )
    )

    ClockDialog(
        state = clockState,
        selection = ClockSelection.HoursMinutes { h, m ->
            actionViewModel.updateSelectedTime(LocalTime.of(h, m))
        },
        config = ClockConfig(
            is24HourFormat = true
        )
    )

    Box(
        modifier = modifier
            .testTag("address_component")
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .then(
                    if (enableScroll) {
                        Modifier.verticalScroll(scrollState)
                    } else {
                        Modifier
                    }
                )
                .padding(bottom = 90.dp)
        ) {
            TextInfo(name = "Время и дата пропажи")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectTimeButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("time_button"),
                    onClick = { clockState.show() },
                    text = actionScreenData.selectedTime?.toString() ?: "00:00",
                    image = R.drawable.ic_ciferblat
                )

                SelectTimeButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("date_button"),
                    onClick = { calendarState.show() },
                    text = actionScreenData.selectedDate?.toString() ?: "Дата",
                    image = R.drawable.calendar
                )
            }


            Spacer(Modifier.height(20.dp))
            TextInfo(name = "Где потерялся?")
            Spacer(Modifier.height(8.dp))
            AddressRow(actionViewModel = actionViewModel)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Удерживайте палец на карте, чтобы поставить точку",
                fontSize = 13.sp,
                color = textHint
            )
            Spacer(Modifier.height(8.dp))
            ProfileMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp)),
                pointClick = { lon, lat ->
                    actionViewModel.updateLongitude(lon)
                    actionViewModel.updateLatitude(lat)
                    actionViewModel.getAddressList(lon, lat)
                },
                cameraLocation = mapCameraLocation
            )

            Spacer(Modifier.height(20.dp))
        }

        NextButton(
            Modifier.align(Alignment.BottomCenter),
            onClick = actionViewModel::goToResultPage,
            enabled = addressFillState
        )
    }
}


@Composable
fun CreateAnnouncement(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .testTag("create_button")
            .background(
                color = buttonPrimary.copy(alpha = if (enabled) 1f else 0.4f),
                shape = RoundedCornerShape(15.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 17.dp),
            text = "Опубликовать",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = if (enabled) 1f else 0.7f)
        )
    }
}


@Preview
@Composable
private fun CreateAnnouncementPreview() {
    CreateAnnouncement {

    }
}

@Composable
fun AddressRow(
    modifier: Modifier = Modifier,
    actionViewModel: ActionViewModel,
) {
    Box(
        modifier = modifier
            .testTag("address_input")
            .fillMaxWidth()
            .border(width = 1.dp, color = BrushColor, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .defaultMinSize(minHeight = 42.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!actionViewModel.addressText.isNullOrEmpty()) {
                Image(
                    painter = painterResource(R.drawable.ic_map_point),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =
                        if (actionViewModel.addressText.length > 50) {
                            actionViewModel.addressText.substring(0, 50) + ".."
                        } else {
                            actionViewModel.addressText
                        },
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    modifier = Modifier
                        .size(14.dp, 14.dp)
                        .clickable {
                            actionViewModel.clearAddressRow()
                        },
                    painter = painterResource(R.drawable.ic_cancel_button),
                    contentDescription = "Отменить"
                )
            }
        }
    }
}

@Preview
@Composable
private fun SelectTimeButtonPreview() {
    SelectTimeButton(
        onClick = {},
        image = R.drawable.ic_ciferblat,
        text = ""
    )
}

@Composable
fun SelectTimeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes image: Int,
    text: String
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .border(
                width = 1.dp,
                color = BrushColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = Color.White
            )
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ActionMainComponent(
    modifier: Modifier = Modifier,
    announcementInfo: ActionScreenData,
    actionViewModel: ActionViewModel
) {
    val scrollState = rememberScrollState()
    val state by actionViewModel.state.collectAsState()
    val actionFillState by actionViewModel.isMainActionComponentState.collectAsState()

    val pickImageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                Log.d("URI", uri.toString())
                actionViewModel.addImage(it)
            }
        }

    Box(
        modifier = modifier
            .testTag("main_component")
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        ) {

            PhotosPager(
                photos = state.selectedImageUris,
                onAddPhotoClick = {
                    pickImageLauncher.launch("image/*")
                },
                onRemovePhotoClick = { uri -> actionViewModel.removeImage(uri) }
            )

            Spacer(Modifier.height(24.dp))
            TextInfo(name = "Кличка")
            Spacer(Modifier.height(8.dp))
            PetTextField(
                modifier = Modifier.testTag("name input"),
                label = "Введите кличку",
                value = announcementInfo.name ?: "",
                onValueChange = {
                    actionViewModel.updateName(it)
                },
            )

            Spacer(Modifier.height(20.dp))
            TextInfo(name = "Вид питомца")
            Spacer(Modifier.height(8.dp))
            PetSelector(actionViewModel, announcementInfo)


            Spacer(Modifier.height(20.dp))
            TextInfo(name = "Порода")
            Spacer(Modifier.height(8.dp))
            PetTextField(
                modifier = Modifier.testTag("breed input"),
                label = "Введите породу",
                value = announcementInfo.breed ?: "",
                onValueChange = {
                    actionViewModel.updateBreed(it)
                },
            )

            Spacer(Modifier.height(20.dp))
            TextInfo(name = "Окрас")
            Spacer(Modifier.height(8.dp))
            PetTextField(
                modifier = Modifier.testTag("color input"),
                label = "Введите окрас",
                value = announcementInfo.color ?: "",
                onValueChange = {
                    actionViewModel.updateColor(it)
                },
            )

            Spacer(Modifier.height(20.dp))
            TextInfo(name = "Гендер")
            Spacer(Modifier.height(8.dp))
            GenderSelector(actionViewModel, announcementInfo)


            Spacer(Modifier.height(20.dp))
            TextInfo(name = "Особые приметы")
            Spacer(Modifier.height(8.dp))
            PetTextField(
                modifier = Modifier
                    .height(105.dp)
                    .testTag("description input"),
                label = "Опишите животного",
                maxLines = 10,
                value = announcementInfo.description ?: "",
                onValueChange = {
                    actionViewModel.updateDescription(it)
                },

                )
            Spacer(Modifier.height(60.dp))

        }
        NextButton(
            Modifier.align(Alignment.BottomCenter),
            onClick = actionViewModel::goToAddressPage,
            enabled = actionFillState
        )
    }
}


@Composable
fun TextInfo(modifier: Modifier = Modifier, name: String) {
    Text(
        text = name,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}


@Composable
fun AddPhotoButtonContent(
    modifier: Modifier = Modifier,
    selectedImageUri: String?,
    onPickClick: () -> Unit
) {
    Box(
        modifier = modifier
            .testTag("image_button")
            .background(color = Color(0xFFE9E9E9), shape = RoundedCornerShape(20.dp))
            .clickable { onPickClick() }
            .fillMaxWidth()
            .height(232.dp)
    ) {
        if (selectedImageUri != null) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("selected_image")
                    .clip(RoundedCornerShape(20.dp))
            )
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("placeholder_image"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier.padding(10.dp),
                    painter = painterResource(R.drawable.ic_uploadpet),
                    contentDescription = "Добавить изображение"
                )
                Spacer(Modifier.height(8.dp))
                Text("Добавить фото питомца", fontSize = 12.sp, color = Ser)
            }
        }
    }
}

@Composable
fun AddPhotoButtonHost(
    modifier: Modifier = Modifier,
    selectedImageUri: String?,
    onImagePicked: (String) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImagePicked(it.toString()) }
    }

    AddPhotoButtonContent(
        modifier = modifier,
        selectedImageUri = selectedImageUri,
        onPickClick = { launcher.launch("image/*") }
    )
}


@Composable
fun GenderSelector(viewModel: ActionViewModel, info: ActionScreenData) {
    val genders = listOf(
        MALE to R.drawable.ic_male,
        FEMALE to R.drawable.ic_female
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        genders.forEach { (id, icon) ->
            GenderButton(
                genderId = id,
                genderDrawableRes = icon,
                selectedGender = info.gender,
                onSelect = { viewModel.updateGender(it) }
            )
        }
    }
}


private const val MALE = 0
private const val FEMALE = 1

@Composable
private fun GenderButton(
    genderId: Int,
    @DrawableRes genderDrawableRes: Int,
    selectedGender: Int?,
    onSelect: (Int) -> Unit
) {
    val bg = when {
        selectedGender == genderId && genderId == MALE -> Color.Blue.copy(alpha = 0.1f)
        selectedGender == genderId && genderId == FEMALE -> Color.Magenta.copy(alpha = 0.1f)
        else -> Color.Transparent
    }
    val isSelected = selectedGender == genderId


    Box(
        modifier = Modifier
            .size(60.dp)
            .testTag("gender_$genderId")
            .semantics(mergeDescendants = true) {
                selected = isSelected
                role = Role.RadioButton
            }
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, BrushColor, shape = RoundedCornerShape(10.dp))
            .clickable { onSelect(genderId) }
    ) {
        Image(
            modifier = Modifier
                .padding(14.dp)
                .align(Alignment.Center),
            painter = painterResource(genderDrawableRes),
            contentDescription = "Гендер"
        )
    }
}


@Composable
fun PetSelector(viewModel: ActionViewModel, announcementInfo: ActionScreenData) {
    val selectedPet = announcementInfo.typeOfPet

    val pets = listOf(
        1 to "Собака",
        0 to "Кот",
        2 to "Другое"
    )

    Row(
        modifier = Modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        pets.forEach { (id, name) ->
            PetButton(
                petId = id,
                petName = name,
                selectedPetId = selectedPet,
                onSelect = { viewModel.updateTypeOfPet(it) }
            )
        }
    }
}

@Composable
fun PetButton(
    petId: Int,
    petName: String,
    selectedPetId: Int?,
    onSelect: (Int) -> Unit
) {
    val selected = selectedPetId == petId

    Box(
        modifier = Modifier
            .testTag("pet_$petId")
            .width(108.dp)
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (selected) Color.Magenta.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, BrushColor, shape = RoundedCornerShape(8.dp))
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = { onSelect(petId) }
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = petName,
            fontSize = 14.sp
        )
    }
}


@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .testTag("next_button")
            .background(
                color = buttonPrimary.copy(alpha = if (enabled) 1f else 0.4f),
                shape = RoundedCornerShape(15.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Text(
            modifier = Modifier.padding(vertical = 17.dp, horizontal = 60.dp),
            text = "Продолжить",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Preview
@Composable
private fun PetButtonPreview() {
    PetButton(
        petId = 0,
        petName = "Кошки",
        selectedPetId = 0
    ) {

    }
}
