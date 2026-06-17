package ui.components.profilebar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.components.placeholder.ShimmerTextPlaceholder
import ui.theme.backgroundColor

@Composable
fun ProfileBarComponent(
    modifier: Modifier = Modifier,
    profileBarState: ProfileBarState,
    cityState: ProfileBarCityState,
    notificationsIsNotRead: Boolean,
    onNotifyClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(top = 20.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onSettingsClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageProfile(
                profileBarState = profileBarState
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                when (cityState) {
                    is ProfileBarCityState.Success -> {
                        Text(
                            text = cityState.city,
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                    }

                    is ProfileBarCityState.Failed -> {
                        Text(
                            text = cityState.message,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }

                    ProfileBarCityState.Idle,
                    ProfileBarCityState.Loading -> {
                        ShimmerTextPlaceholder(
                            modifier = Modifier
                                .width(96.dp)
                                .height(16.dp)
                        )
                    }
                }

                when (profileBarState) {
                    is ProfileBarState.Success -> {
                        Text(
                            text = profileBarState.userDataInfo.name,
                            fontSize = 20.sp,
                        )
                    }

                    is ProfileBarState.Loading -> {
                        ShimmerTextPlaceholder()
                    }

                    is ProfileBarState.Failed -> {
                        Text(
                            text = "Не удалось загрузить пользователя",
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }

                    else -> {}
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = if (!notificationsIsNotRead) {
                    painterResource(R.drawable.ic_bell)
                } else painterResource(R.drawable.isnotread),
                contentDescription = "Уведомления",
                tint = Color.Gray,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFEDEDED))
                    .clickable { onNotifyClick() }
                    .padding(12.dp)
            )
        }
    }
}
