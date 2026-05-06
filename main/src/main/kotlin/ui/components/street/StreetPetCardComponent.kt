package ui.components.street

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import domain.models.StreetPetPreviewModel
import ui.BASE_URL

@Composable
fun StreetPetCardComponent(
    modifier: Modifier = Modifier,
    streetPetPreviewModel: StreetPetPreviewModel
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                modifier = Modifier
                    .size(170.dp)
                    .clip(RoundedCornerShape(13.dp)),
                contentScale = ContentScale.Crop,
                model = "$BASE_URL/api/image/${streetPetPreviewModel.image}",
                placeholder = painterResource(R.drawable.image),
                error = painterResource(R.drawable.image),
                contentDescription = null,
                onError = {
                    println("Image loading failed: ${it.result.throwable?.message}")
                    it.result.throwable?.printStackTrace()
                },
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = streetPetPreviewModel.district ?: "Неизвестно",
                fontSize = 12.sp
            )
            Text(
                text = streetPetPreviewModel.street ?: "Неизвестно",
                fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))
            Row {
                val info = formatMinutesAgo(streetPetPreviewModel.minutesAgo)
                StreetPetChip(
                    information = info
                )
                Spacer(Modifier.width(5.dp))
                StreetPetChip(
                    information = streetPetPreviewModel.date
                )
            }
        }
    }
}

private fun formatMinutesAgo(minutesAgo: Long): String {
    return when {
        minutesAgo < 60 -> "${minutesAgo} мин назад"
        minutesAgo < 60 * 24 -> "${minutesAgo / 60} ч назад"
        else -> "${minutesAgo / (60 * 24)} дн назад"
    }
}

@Preview
@Composable
private fun StreetPetCardComponentPreview() {
    StreetPetCardComponent(
        streetPetPreviewModel = StreetPetPreviewModel(
            id = "dasd",
            street = "ул. Парижской Коммуны, 1",
            district = "Центральный",
            time = "20 минут назад",
            date = "28/02",
            image = "asdasdasd",
            minutesAgo = 20L
        )
    )
}