package ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import domain.models.Creator
import domain.models.FoundPetInfo
import ui.components.MapComponent
import ui.theme.backgroundColor
import ui.theme.textHint
import ui.viewModel.FilterViewModel


@Composable
fun FoundPetScreen(
    modifier: Modifier = Modifier,
    goBackClick: () -> Unit,
    viewModel: FilterViewModel
) {
    val petInfo by viewModel.petInfoState.collectAsState()

    if (petInfo == null) {
        Box(
            modifier = modifier
                .background(backgroundColor)
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Загрузка...", color = Color.Gray, fontSize = 14.sp)
        }
        return
    } else {
        val info = petInfo!!
//        Column(
//            modifier = modifier
//                .background(color = backgroundColor)
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//        ) {
//            PetImageComponent(
//                goBackClick = goBackClick,
//                foundPetInfo = petInfo!!
//            )
//            Column(
//                modifier = modifier
//                    .fillMaxHeight()
//                    .background(
//                        color = Color.White,
//                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
//                    )
//                    .padding(horizontal = 16.dp, vertical = 16.dp)
//            ) {
//                PetInfoComponent(foundPetInfo = petInfo!!)
//                Spacer(Modifier.height(32.dp))
//                EventDateComponent(advertState = "${petInfo!!.dateInfo.date} ${petInfo!!.dateInfo.time}")
//                Spacer(Modifier.height(32.dp))
//                WhereFindComponent(foundPetInfo = info)
//                Spacer(Modifier.height(32.dp))
//                UserInfoComponent(creatorInfo = info.creator)
//            }
//            ButtonComponent(
//                color = buttonPrimary,
//                text = "Это мой питомец",
//                textColor = Color.White,
//                enabled = true,
//                radius = 40.dp,
//                onClick = {}
//            )
//        }

    }
}

@Composable
fun PetInfoComponent(
    modifier: Modifier = Modifier,
    foundPetInfo: FoundPetInfo
) {
    Column {
        Text(
            text = "Особый приметы",
            fontSize = 16.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = foundPetInfo.petInfo.description,
            fontSize = 14.sp,
            color = textHint
        )
        Spacer(Modifier.height(12.dp))
        ParameterText(
            descr = "Порода",
            value = foundPetInfo.petInfo.breed
        )
        Spacer(Modifier.height(8.dp))
        ParameterText(
            descr = "Пол",
            value = when (foundPetInfo.petInfo.gender) {
                0 -> "Мальчик"
                1 -> "Девочка"
                else -> "Неизвестно"
            }
        )
        Spacer(Modifier.height(8.dp))
        ParameterText(
            descr = "Окрас",
            value = foundPetInfo.petInfo.breed
        )
    }
}

@Composable
fun ParameterText(
    modifier: Modifier = Modifier,
    descr: String,
    value: String
) {
    Row {
        Text(
            text = "${descr}:",
            fontSize = 14.sp,
            color = textHint
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
        )
    }
}


@Composable
fun UserInfoComponent(
    modifier: Modifier = Modifier,
    creatorInfo: Creator,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding()
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(42.dp),
                contentScale = ContentScale.Crop,
                model = "$BASE_URL/api/image/${creatorInfo.avatarPath}",
                placeholder = painterResource(R.drawable.ic_lapa),
                error = painterResource(R.drawable.ic_lapa),
                contentDescription = null,
                onError = {
                    println("Image loading failed: ${it.result.throwable?.message}")
                    it.result.throwable?.printStackTrace()
                },
            )
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = creatorInfo.firstName,
                    fontSize = 16.sp
                )

                Text(
                    text = "Красноярск",
                    color = textHint
                )
            }
        }
    }
}


@Composable
fun PetImageComponent(
    modifier: Modifier = Modifier,
    goBackClick: () -> Unit,
    foundPetInfo: FoundPetInfo
) {
    Box {
        AsyncImage(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .fillMaxWidth()
                .height(360.dp),
            contentScale = ContentScale.Crop,
            model = "${foundPetInfo.imagePath}",
            placeholder = painterResource(R.drawable.ic_dog),
            error = painterResource(R.drawable.ic_dog),
            contentDescription = null,
            onError = {
                println("Image loading failed: ${it.result.throwable?.message}")
                it.result.throwable?.printStackTrace()
            },
        )

        Image(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp)
                .padding(top = 60.dp)
                .clickable {
                    goBackClick()
                },
            painter = painterResource(R.drawable.ic_back_found),
            contentDescription = "Кнопка назад"
        )
    }
}

@Composable
fun WhereFindComponent(
    modifier: Modifier = Modifier,
    foundPetInfo: FoundPetInfo,
    isMapSheetOpen: Boolean,
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                text = "Где нашли",
                fontSize = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            if (!isMapSheetOpen) {
                MapComponent(
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .height(200.dp),
                    longitude = foundPetInfo.lon,
                    latitude = foundPetInfo.lat,
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                text = "${foundPetInfo.district ?: "Октябрьский"}, ${foundPetInfo.street} ${foundPetInfo.house}",
                fontSize = 14.sp
            )
        }
    }
}
