package ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.core.R
import ui.BASE_URL
import ui.components.default_component.SocialTextFieldComponent
import ui.components.default_component.TextFieldData
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.model.SettingsButton
import ui.model.SettingsButtonComponent
import ui.model.getContact
import ui.theme.EditTextColor
import ui.theme.Ser
import ui.viewModel.ProfileSettingsViewModel

@Composable
fun ProfileSettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    exit: () -> Unit,
    settingsViewModel: ProfileSettingsViewModel
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            settingsViewModel.setUri(uri.toString())
            settingsViewModel.updateImage()
        }
    }

    LazyColumn(Modifier.fillMaxHeight()) {
        item {
            ToolBar(
                modifier = Modifier,
                toolBarInfo = ToolBarInfo(
                    backArrow = true,
                ),
                onBackClick = {
                    onBackClick()
                },
            )
        }
        item {
            BottomSettingsMainContent(
                settingsViewModel = settingsViewModel,
                exit = {
                    exit()
                    settingsViewModel.logout()
                },
                setImage = {
                    launcher.launch("image/*")
                }
            )
        }

    }

}

@Composable
fun BottomSettingsMainContent(
    modifier: Modifier = Modifier,
    settingsViewModel: ProfileSettingsViewModel,
    exit: () -> Unit,
    setImage: () -> Unit
) {
    val screenState = settingsViewModel.profileInfoState.collectAsState()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color.White)
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImageComponent(settingsViewModel = settingsViewModel) {
                setImage()
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Изменить фото",
                color = Ser,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(16.dp))
            SettingsTextField(
                value = screenState.value.name,
                label = "Никнейм",
                onValueChange = settingsViewModel::setName,
            )
            Spacer(Modifier.height(8.dp))
            SettingsTextField(
                value = screenState.value.description,
                label = "Описание",
                onValueChange = settingsViewModel::setDescription
            )
            Spacer(Modifier.height(32.dp))
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = "Контакты",
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))

            SocialTextFieldComponent(
                value = screenState.value.getContact(0),
                textFieldData = TextFieldData("", "Вставьте ссылку на VK"),
                onValueChange = settingsViewModel::addVk,
                icon = R.drawable.ic_vk
            )
            Spacer(Modifier.height(8.dp))
            SocialTextFieldComponent(
                value = screenState.value.getContact(1),
                textFieldData = TextFieldData("", "Вставьте ссылку на WhatsApp"),
                onValueChange = settingsViewModel::addWhatsApp,
                icon = R.drawable.ic_whatsapp
            )
            Spacer(Modifier.height(8.dp))
            SocialTextFieldComponent(
                value = screenState.value.getContact(2),
                textFieldData = TextFieldData("", "Вставьте ссылку на Telegram"),
                onValueChange = settingsViewModel::addTelegram,
                icon = R.drawable.ic_tg
            )
            Spacer(Modifier.height(32.dp))
            VerifyComponent() {

            }
            Spacer(Modifier.height(20.dp))
            BottomInfoComponent(
                profileSettingsViewModel = settingsViewModel,
                exit = {
                    exit()
                }
            )
        }
    }
}

@Composable
fun BottomInfoComponent(
    modifier: Modifier = Modifier,
    profileSettingsViewModel: ProfileSettingsViewModel,
    exit: () -> (Unit)
) {
    val listOfButtons = listOf(
        SettingsButton(
            title = "Настройки уведомлений",
            type = SettingsButton.ButtonType.isSwitcherButton,
            image = R.drawable.ic_notification_settings
        ),
        SettingsButton(
            title = "Выйти",
            type = SettingsButton.ButtonType.isExitButton,
            image = R.drawable.ic_exit_notifications
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color(0xFFF8F8F8))
    ) {
        Column {
            listOfButtons.forEach {
                SettingsButtonComponent(
                    settingsButton = it,
                    settingsViewModel = profileSettingsViewModel,
                    contactWithMe = {

                    },
                    exitAction = {
                        exit()
                    },
                )
            }
        }
    }
}


@Composable
fun ProfileImageComponent(
    modifier: Modifier = Modifier,
    settingsViewModel: ProfileSettingsViewModel,
    setImage: () -> Unit
) {
    val screenState = settingsViewModel.profileInfoState.collectAsState()
    val uri = screenState.value.uri

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                setImage()
            }
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("$BASE_URL/api/image/${screenState.value.uri}")
                .crossfade(true)
                .build(),
            contentDescription = "Фотография профиля",
            modifier = Modifier
                .size(80.dp),
            contentScale = ContentScale.Crop,
            loading = {
                ShimmerImagePlaceholder(
                    modifier = Modifier
                        .matchParentSize()
                )
            },
            error = {
                ShimmerImagePlaceholder(
                    modifier = Modifier
                        .matchParentSize()
                )
            }
        )
    }
}

@Composable
fun SettingsTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Ser
            )
        },
        textStyle = TextStyle(
            fontSize = 14.sp
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = EditTextColor,
            unfocusedContainerColor = EditTextColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        maxLines = 1,
    )
}

@Composable
fun VerifyComponent(
    modifier: Modifier = Modifier,
    onVerifyClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF3787FF), Color(0xFF0A3BEA)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .clickable { onVerifyClick() }
    ) {
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 12.dp,
                bottom = 12.dp,
                end = 80.dp
            )
        ) {
            Column(Modifier.padding(top = 16.dp)) {
                Text(
                    text = "Верификация",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Пройдите верификацию чтобы \nполучить полный доступ",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }

        Image(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 15.dp, y = 15.dp)
                .rotate(-15f),
            painter = painterResource(R.drawable.ic_lapa),
            contentDescription = null
        )

    }
}


