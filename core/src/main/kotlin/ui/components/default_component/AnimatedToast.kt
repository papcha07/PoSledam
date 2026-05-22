package ui.components.default_component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AnimatedToast(
    message: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFE3F2FD),
    textColor: Color = Color.Black,
    duration: Long = 2500L,
    onDismiss: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(message) {
        visible = true
        delay(duration)
        visible = false
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        exit = fadeOut() + slideOutVertically { it / 2 },
        enter = fadeIn() + slideInVertically { it / 2 }
    ) {
        Box(
            modifier = modifier
                .testTag("toast_message")
                .padding(16.dp)
                .padding(top = 40.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .align(Alignment.Center)
            ) {
                Text(
                    text = message,
                    color = textColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}