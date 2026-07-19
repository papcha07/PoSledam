package ui.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.core.R
import domain.user.model.User
import ui.components.default_component.DefaultButton
import ui.components.default_component.SocialTextFieldComponent
import ui.components.default_component.TextFieldData
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.model.SettingsButton
import ui.model.SettingsButtonComponent
import ui.theme.EditTextColor
import ui.theme.Ser
import ui.viewModel.ProfileSettingsViewModel

@Composable
fun ProfileSettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onLegalInfoClick: () -> Unit,
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
    val screenState by settingsViewModel.profileInfoState.collectAsState()

    LaunchedEffect(Unit) {
        settingsViewModel.observeUser()
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
                userDataUi = screenState,
                exit = {
                    exit()
                    settingsViewModel.logout()
                },
                setImage = {
                    launcher.launch("image/*")
                },
                setDescription = settingsViewModel::setDescription,
                setName = settingsViewModel::setName,
                addVk = settingsViewModel::addVk,
                addTg = settingsViewModel::addTelegram,
                addWhatsApp = settingsViewModel::addWhatsApp,
                save = settingsViewModel::updateUserInfo,
                onLegalInfoClick = onLegalInfoClick
            )
        }

    }

}

@Composable
fun BottomSettingsMainContent(
    modifier: Modifier = Modifier,
    userDataUi: User,
    exit: () -> Unit,
    setImage: () -> Unit,
    setDescription: (String) -> Unit,
    setName: (String) -> Unit,
    addVk: (String) -> Unit,
    addTg: (String) -> Unit,
    addWhatsApp: (String) -> Unit,
    save: () -> Unit,
    onLegalInfoClick: () -> Unit
) {
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
            ProfileImageComponent(
                userDataUi = userDataUi,
                setImage = setImage
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Изменить фото",
                color = Ser,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(16.dp))
            SettingsTextField(
                value = userDataUi.name,
                label = "Никнейм",
                onValueChange = setName,
            )
            Spacer(Modifier.height(8.dp))
            SettingsTextField(
                value = userDataUi.description ?: "",
                label = "Описание",
                onValueChange = setDescription
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
                value = userDataUi.vk ?: "",
                textFieldData = TextFieldData("", "Вставьте ссылку на VK"),
                onValueChange = addVk,
                icon = R.drawable.ic_vk
            )
            Spacer(Modifier.height(8.dp))
            SocialTextFieldComponent(
                value = userDataUi.wh ?: "",
                textFieldData = TextFieldData("", "Вставьте ссылку на MAX"),
                onValueChange = addWhatsApp,
                icon = R.drawable.ic_whatsapp
            )
            Spacer(Modifier.height(8.dp))
            SocialTextFieldComponent(
                value = userDataUi.tg ?: "",
                textFieldData = TextFieldData("", "Вставьте ссылку на Telegram"),
                onValueChange = addTg,
                icon = R.drawable.ic_tg
            )
            Spacer(Modifier.height(32.dp))
//            VerifyComponent(onVerifyClick = {})
            DefaultButton(
                onClick = save,
                text = "Сохранить"
            )
            Spacer(Modifier.height(20.dp))
            BottomInfoComponent(
                exit = exit,
                onLegalInfoClick = onLegalInfoClick
            )
        }
    }
}

@Composable
fun BottomInfoComponent(
    modifier: Modifier = Modifier,
    onLegalInfoClick: () -> Unit,
    exit: () -> Unit
) {
    val listOfButtons = listOf(
        SettingsButton(
            title = "Правовая информация",
            type = SettingsButton.ButtonType.isArrowButton,
            image = R.drawable.ic_settings
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
                    contactWithMe = onLegalInfoClick,
                    exitAction = exit,
                    notificationState = false,
                    updateNotificationState = {}
                )
            }
        }
    }
}


@Composable
fun ProfileImageComponent(
    modifier: Modifier = Modifier,
    userDataUi: User,
    setImage: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                setImage()
            }
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userDataUi.avatarPath)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "Фотография профиля",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop,
            onError = { error ->
                Log.e(
                    "AvatarImage",
                    "Ошибка загрузки аватарки: ${error.result.throwable.message}",
                    error.result.throwable
                )
            },
            loading = {
                ShimmerImagePlaceholder(
                    modifier = Modifier.matchParentSize()
                )
            },
            error = {
                Image(
                    painter = painterResource(R.drawable.avatar),
                    contentDescription = "Фотография профиля (ошибка)",
                    modifier = Modifier.matchParentSize()
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
