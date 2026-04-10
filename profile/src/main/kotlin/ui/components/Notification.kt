package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.theme.Ser
import ui.theme.buttonPrimary

@Composable
fun Notification(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(376.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(156.dp, 165.dp),
                painter = painterResource(R.drawable.ic_notification_image),
                contentDescription = null
            )
            Spacer(Modifier.height(22.dp))
            Text(
                text = "Объявление подано",
                fontSize = 24.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Включите уведомления чтобы не пропустить",
                fontSize = 16.sp,
                color = Ser
            )
            Text(
                text = "важную информацию о вашем питомце",
                fontSize = 16.sp,
                color = Ser
            )
            Spacer(Modifier.height(22.dp))

            ButtonComponent(
                modifier = Modifier.padding(horizontal = 19.dp),
                color = buttonPrimary,
                text = "Подписаться",
                textColor = Color.White,
                enabled = true,
                radius = 14.dp,
                onClick = onClick
            )
        }
    }
}

@Preview
@Composable
private fun NotificationPreview() {
    Notification() {

    }
}