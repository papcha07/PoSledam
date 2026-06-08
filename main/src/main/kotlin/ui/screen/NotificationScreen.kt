package ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import domain.notification.Notification
import kotlinx.coroutines.delay
import toFormattedDate
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.screen.mainScreen.MainScreenViewModel
import ui.theme.Purple80
import ui.theme.backgroundColor

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    navigateToMainScreen: () -> Unit,
    mainScreenViewModel: MainScreenViewModel
) {
    val notificationList by mainScreenViewModel.notificationState.collectAsState()
    LaunchedEffect(Unit) {
        mainScreenViewModel.markAllNotifications()
    }
    Column(modifier = modifier.background(backgroundColor)) {
        ToolBar(
            toolBarInfo = ToolBarInfo(
                title = "Уведомления",
                backArrow = true,
                nextRoute = "",
                prevRoute = "",
            ),
            onBackClick = {
                navigateToMainScreen()
            },
            onActionClick = {

            }
        )
        NotificationMainComponent(
            notificationList = notificationList,
            mainScreenViewModel = mainScreenViewModel
        )
    }
}


@Composable
fun NotificationMainComponent(
    modifier: Modifier = Modifier,
    notificationList: List<Notification>,
    mainScreenViewModel: MainScreenViewModel
) {
    LazyColumn {
        items(
            items = notificationList,
            key = { it.id }
        ) { item ->

            var visible by remember { mutableStateOf(true) }

            AnimatedVisibility(
                visible = visible,
                exit = shrinkVertically() + fadeOut()
            ) {
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            visible = false
                            true
                        } else false
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Purple80, RoundedCornerShape(20.dp))
                                .padding(end = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                ) {
                    NotificationComponent(notificationInfo = item)
                }
            }

            LaunchedEffect(visible) {
                if (!visible) {
                    delay(300)
                    mainScreenViewModel.deleteById(item.id)
                }
            }
        }

    }


}

@Composable
fun NotificationComponent(
    modifier: Modifier = Modifier,
    notificationInfo: Notification,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                shape = RoundedCornerShape(20.dp)
            )

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(
                    modifier = Modifier
                        .size(35.dp, 35.dp)
                        .clip(RoundedCornerShape(38.dp)),
                    painter = when (notificationInfo.type) {
                        0 -> {
                            painterResource(R.drawable.ic_chelovek_notif)
                        }

                        1 -> {
                            painterResource(R.drawable.ic_galochka_notification)
                        }

                        else -> {
                            painterResource(R.drawable.ic_lapa)
                        }
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Column(Modifier.padding(start = 10.dp)) {
                    Text(
                        text = notificationInfo.title,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = notificationInfo.body,
                        fontSize = 12.sp,
                    )
                }
            }

            Column {
                Text(
                    text = notificationInfo.time.toFormattedDate(),
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (notificationInfo.isRead) Color.Green else Color.Red,
                            shape = CircleShape
                        )
                        .align(Alignment.End)
                )
            }
        }
    }
}
