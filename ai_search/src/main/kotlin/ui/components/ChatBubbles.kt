package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import ui.theme.buttonPrimary

internal val AccentColor = buttonPrimary                 // 0xFF8C6BF0
internal val ChatTextColor = Color(0xFF1E1E1E)           // cher
internal val ChatMutedColor = Color(0xFF9B9B9B)
private val AssistantShape = RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp)

/** Небольшая серая метка даты/времени по центру. */
@Composable
fun ChatDateLabel(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 12.sp, color = ChatMutedColor)
    }
}

/** Реплика помощника слева: белый пузырь с мягкой тенью, без аватара. */
@Composable
fun AssistantMessage(
    text: String?,
    modifier: Modifier = Modifier,
    warning: Boolean = false,
    content: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .shadow(6.dp, AssistantShape, clip = false, spotColor = Color(0x1A000000))
                .clip(AssistantShape)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (!text.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.Top) {
                    if (warning) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bell),
                            contentDescription = null,
                            tint = AccentColor,
                            modifier = Modifier
                                .padding(end = 8.dp, top = 1.dp)
                                .size(16.dp)
                        )
                    }
                    Text(text = text, fontSize = 14.sp, color = ChatTextColor, lineHeight = 20.sp)
                }
            }
            if (content != null) {
                if (!text.isNullOrBlank()) Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}

/** Сообщение пользователя справа: фото в толстой рамке акцентного цвета. */
@Composable
fun UserPhotoMessage(imageModel: Any?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 240.dp)
                .shadow(6.dp, RoundedCornerShape(20.dp), clip = false, spotColor = Color(0x1A000000))
                .clip(RoundedCornerShape(20.dp))
                .background(AccentColor)
                .padding(9.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(210.dp)
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                model = imageModel,
                placeholder = painterResource(R.drawable.ic_dog),
                error = painterResource(R.drawable.ic_dog),
                contentDescription = null
            )
        }
    }
}

/** «Печатает…» / ожидание — белый пузырь слева со спиннером. */
@Composable
fun TypingMessage(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .shadow(6.dp, AssistantShape, clip = false, spotColor = Color(0x1A000000))
                .clip(AssistantShape)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = AccentColor
            )
            Spacer(Modifier.width(12.dp))
            Text(text = text, fontSize = 14.sp, color = ChatTextColor)
        }
    }
}
