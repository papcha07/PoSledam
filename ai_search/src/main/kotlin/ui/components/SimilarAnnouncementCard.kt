package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import domain.models.SimilarAnnouncement

/** Современная белая мини-карточка похожего объявления с бейджем типа и стрелкой. */
@Composable
fun SimilarAnnouncementCard(
    modifier: Modifier = Modifier,
    announcement: SimilarAnnouncement,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), clip = false, spotColor = Color(0x14000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            model = aiImageModel(announcement.imageUrl),
            placeholder = painterResource(R.drawable.ic_dog),
            error = painterResource(R.drawable.ic_dog),
            contentDescription = null
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            announcement.type?.let { TypeBadge(type = it) }
            Spacer(Modifier.height(6.dp))
            Text(
                text = announcement.breed?.takeIf { it.isNotBlank() } ?: "Без породы",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E1E1E),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val address = listOfNotNull(
                announcement.street?.takeIf { it.isNotBlank() },
                announcement.house?.takeIf { it.isNotBlank() },
                announcement.district?.takeIf { it.isNotBlank() }
            ).joinToString(", ")
            if (address.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_map_point),
                        contentDescription = null,
                        tint = AccentColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = address,
                        fontSize = 12.sp,
                        color = Color(0xFF787878),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(Modifier.width(6.dp))

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF2F0FB)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.right_arrow),
                contentDescription = "Открыть",
                tint = AccentColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun TypeBadge(type: Int) {
    val (text, color) = when (type) {
        0 -> "Находка" to Color(0xFF2E9E5B)
        1 -> "Пропажа" to Color(0xFFEB384E)
        2 -> "Уличное" to Color(0xFF3A72D8)
        else -> "Объявление" to Color(0xFF9B9B9B)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = text, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
