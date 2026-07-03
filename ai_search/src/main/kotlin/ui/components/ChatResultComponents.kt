package ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import domain.models.AiSearchResult
import domain.models.SimilarAnnouncement

/** Ответ помощника с найденными похожими объявлениями. */
@Composable
fun AssistantResultMessage(
    text: String,
    results: List<SimilarAnnouncement>,
    onOpen: (SimilarAnnouncement) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AssistantMessage(text = text)
        Spacer(Modifier.height(6.dp))
        results.forEach { item ->
            SimilarAnnouncementCard(
                announcement = item,
                onClick = { onOpen(item) }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

/**
 * Запись истории как компактная белая мини-карточка в чате.
 * Результаты уже вложены в [entry] (backend не отдаёт requestId в списке истории),
 * поэтому по клику раскрываем их прямо здесь.
 */
@Composable
fun HistoryChatMessage(
    entry: AiSearchResult,
    onOpen: (SimilarAnnouncement) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val expandable = entry.results.isNotEmpty()

    Column(modifier = modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp), clip = false, spotColor = Color(0x14000000))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(enabled = expandable) { expanded = !expanded }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                model = aiImageModel(entry.searchImagePath),
                placeholder = painterResource(R.drawable.ic_dog),
                error = painterResource(R.drawable.ic_dog),
                contentDescription = null
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = summary(entry),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E1E1E)
                )
                Spacer(Modifier.height(3.dp))
                Text(text = relativeTime(entry.createdAt), fontSize = 12.sp, color = Color(0xFF9B9B9B))
            }
            if (expandable) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF2F0FB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.right_arrow),
                        contentDescription = "Посмотреть",
                        tint = AccentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        AnimatedVisibility(visible = expanded && expandable) {
            Column {
                Spacer(Modifier.height(8.dp))
                entry.results.forEach { item ->
                    SimilarAnnouncementCard(
                        announcement = item,
                        onClick = { onOpen(item) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

private fun summary(entry: AiSearchResult): String = when {
    entry.hasError -> "Поиск не удался"
    entry.results.isEmpty() -> "Совпадений не найдено"
    entry.results.size == 1 -> "1 похожее объявление"
    else -> "Похожих объявлений: ${entry.results.size}"
}

private fun relativeTime(iso: String): String {
    return runCatching {
        val created = java.time.OffsetDateTime.parse(iso)
        val minutes = java.time.Duration.between(created, java.time.OffsetDateTime.now()).toMinutes()
        when {
            minutes < 1 -> "только что"
            minutes < 60 -> "$minutes мин назад"
            minutes < 60 * 24 -> "${minutes / 60} ч назад"
            minutes < 60 * 24 * 7 -> "${minutes / (60 * 24)} дн назад"
            else -> created.atZoneSameInstant(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }
    }.getOrDefault(iso.take(10))
}
