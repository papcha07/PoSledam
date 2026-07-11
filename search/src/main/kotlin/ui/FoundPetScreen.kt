package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ui.components.placeholder.ShimmerAsyncImage
import ui.theme.textHint

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
            value = foundPetInfo.petInfo.color
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
                model = "${creatorInfo.avatarPath}",
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
    val imagePaths = foundPetInfo.imagePaths
        .ifEmpty { listOfNotNull(foundPetInfo.imagePath) }
        .filter { it.isNotBlank() }
    val pagerState = rememberPagerState { imagePaths.size }

    Box(modifier = modifier) {
        if (imagePaths.size > 1) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                    beyondViewportPageCount = 1
                ) { page ->
                    ShimmerAsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        model = imagePaths[page].toImageModel(),
                        contentDescription = null,
                        onError = {
                            println("Image loading failed: ${it.result.throwable?.message}")
                            it.result.throwable?.printStackTrace()
                        },
                    )
                }

                PetImagePagerIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    currentIndex = pagerState.currentPage,
                    totalCount = imagePaths.size
                )
            }
        } else {
            ShimmerAsyncImage(
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
                model = imagePaths.firstOrNull()?.toImageModel().orEmpty(),
                contentDescription = null,
                onError = {
                    println("Image loading failed: ${it.result.throwable?.message}")
                    it.result.throwable?.printStackTrace()
                },
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 4.dp, top = 48.dp)
                .size(48.dp)
                .clickable {
                    goBackClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(R.drawable.ic_back_found),
                contentDescription = "Кнопка назад"
            )
        }
    }
}

@Composable
private fun PetImagePagerIndicator(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    totalCount: Int
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.38f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalCount) { index ->
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (index == currentIndex) 16.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentIndex) Color.White else Color.White.copy(alpha = 0.55f)
                    )
            )
        }

        Text(
            modifier = Modifier.padding(start = 3.dp),
            text = "${currentIndex + 1}/$totalCount",
            color = Color.White,
            fontSize = 11.sp
        )
    }
}

private fun String.toImageModel(): String {
    return when {
        startsWith("http://") || startsWith("https://") || startsWith("content://") -> this
        else -> "$BASE_URL/api/image/${trimStart('/')}"
    }
}

@Composable
fun WhereFindComponent(
    modifier: Modifier = Modifier,
    foundPetInfo: FoundPetInfo,
    announcementType: Int,
    isMapSheetOpen: Boolean,
    onMapTouchStateChanged: (Boolean) -> Unit = {}
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
                text = if (announcementType == 0) "Где нашли" else "Где потерялся",
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
                    onTouchStateChanged = onMapTouchStateChanged
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
