package ui.model

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.theme.buttonPrimary


data class SettingsButton(
    val title: String,
    val type: ButtonType,
    @DrawableRes val image: Int
) {
    enum class ButtonType {
        isExitButton,
        isSwitcherButton,
        isArrowButton
    }
}

@Composable
fun SettingsButtonComponent(
    modifier: Modifier = Modifier,
    settingsButton: SettingsButton,
    notificationState: Boolean,
    contactWithMe: () -> Unit,
    exitAction: () -> Unit,
    updateNotificationState: (Boolean) -> Unit
) {
    val isSwitcher = settingsButton.type == SettingsButton.ButtonType.isSwitcherButton

    val rowModifier = if (isSwitcher) {
        modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp)
    } else {
        modifier
            .fillMaxWidth()
            .clickable {
                when (settingsButton.type) {
                    SettingsButton.ButtonType.isExitButton -> exitAction()
                    SettingsButton.ButtonType.isArrowButton -> contactWithMe()
                    else -> {}
                }
            }
            .padding(horizontal = 14.dp, vertical = 14.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(settingsButton.image),
            contentDescription = null
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = settingsButton.title,
            fontSize = 15.sp,
            color = if (settingsButton.title == "Выйти") Color(0xFFDA2F2F) else Color.Black
        )

        Spacer(Modifier.weight(1f))

        if (settingsButton.type == SettingsButton.ButtonType.isArrowButton) {
            Image(
                painter = painterResource(R.drawable.right_arrow),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp) // немного увеличим размер для видимости
            )
        } else if (isSwitcher) {
            Switch(
                checked = notificationState,
                onCheckedChange = updateNotificationState,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = buttonPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )
        }
    }
}

