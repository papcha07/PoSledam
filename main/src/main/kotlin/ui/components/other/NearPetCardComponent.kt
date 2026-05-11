package ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.R
import domain.models.StreetPetPreviewModel
import ui.BASE_URL

@Composable
fun NearPetCardComponent(
    modifier: Modifier = Modifier,
    streetPetPreviewModel: StreetPetPreviewModel,
    openStreetDetails: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                openStreetDetails(streetPetPreviewModel.id)
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {

            AsyncImage(
                modifier = Modifier
                    .height(332.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
                model = "$BASE_URL/api/image/${streetPetPreviewModel.image}",
                placeholder = painterResource(R.drawable.ic_dog),
                error = painterResource(R.drawable.ic_dog),
                contentDescription = null,
                onError = {
                    println("Image loading failed: ${it.result.throwable?.message}")
                    it.result.throwable?.printStackTrace()
                },
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 20.dp)
            ) {
                Row {
                    PetInfoComponent(
                        text = formatMinutesAgo(streetPetPreviewModel.minutesAgo)
                    )
                    Spacer(Modifier.width(14.dp))
                    PetInfoComponent(
                        text = streetPetPreviewModel.date
                    )
                }
                Spacer(Modifier.height(8.dp))
                PetInfoComponent(
                    text = streetPetPreviewModel.street ?: "Неизвестно"
                )
            }

        }
    }
}

@Preview
@Composable
fun NearPetMainComponentPreview() {
    NearPetCardComponent(
        Modifier,
        streetPetPreviewModel = StreetPetPreviewModel(
            id = "dasd",
            street = "ул. Парижской Коммуны, 1",
            district = "Центральный",
            time = "20 минут назад",
            date = "28/02",
            image = "asdasdasd",
            minutesAgo = 20L
        )
    ) {

    }
}

private fun formatMinutesAgo(minutesAgo: Long): String {
    return when {
        minutesAgo < 60 -> "${minutesAgo} мин назад"
        minutesAgo < 60 * 24 -> "${minutesAgo / 60} ч назад"
        else -> "${minutesAgo / (60 * 24)} дн назад"
    }
}