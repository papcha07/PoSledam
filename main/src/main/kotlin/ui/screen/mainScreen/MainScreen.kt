package ui.screen.mainScreen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.core.R
import domain.models.PetUiPreview
import domain.models.StreetPetPreviewModel
import ui.PetCardComponent
import ui.SearchPetCardShimmerPlaceholder
import ui.components.default_component.DefaultButton
import ui.components.other.NearPetCardComponent
import ui.components.other.PetInfoComponent
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.components.placeholder.ShimmerTextPlaceholder
import ui.model.StoryId
import ui.model.StoryInfo
import ui.model.storyInfoList
import ui.theme.backgroundColor
import ui.theme.buttonPrimary

@Composable
fun MainScreen(
    navigateToStreetPetScreen: () -> Unit,
    navigateToCameraScreen: () -> Unit,
    navigateToStoryScreen: (StoryId) -> Unit,
    goToDetailsPetScreen: (String, Int) -> Unit,
    mainScreenViewModel: MainScreenViewModel,
) {
    val latestStreetPetState by mainScreenViewModel.latestStreetPetState.collectAsStateWithLifecycle()
    val todayMissingPets = mainScreenViewModel.todayMissingPets.collectAsLazyPagingItems()

    MainPermissionHandler(
        viewModel = mainScreenViewModel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = backgroundColor)
            .border(
                width = 1.dp,
                color = Color(0xFFF1F1F1)
            ),
    ) {
        TargetPetsSection(
            latestStreetPetState = latestStreetPetState,
            navigateToStreetPetScreen = navigateToStreetPetScreen,
            navigateToCameraScreen = navigateToCameraScreen
        )

        StorySectionComponent(
            modifier = Modifier,
            storyInfoList = storyInfoList,
            navigateToStoryScreen = navigateToStoryScreen
        )
        Spacer(Modifier.height(4.dp))

        TodayMissingPetsSection(
            pets = todayMissingPets,
            goToDetailsPetScreen = goToDetailsPetScreen
        )
        Spacer(Modifier.height(16.dp))
    }
}

//@Composable
//fun GetPhotoComponent(
//    modifier: Modifier = Modifier,
//    onClick: () -> Unit
//) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(80.dp)
//            .background(
//                brush = Brush.linearGradient(
//                    colors = listOf(Color(0xFF4F52EC), Color(0xFF97BBFF)),
//                    start = Offset(0f, 0f),
//                    end = Offset(1000f, 1000f)
//                ),
//                shape = RoundedCornerShape(20.dp)
//            )
//            .clip(RoundedCornerShape(20.dp))
//    ) {
//
//        Column(
//            modifier = Modifier.padding(top = 12.dp, start = 16.dp)
//        ) {
//
//            Text(
//                text = "Замечен!",
//                fontSize = 14.sp,
//                color = Color.White
//            )
//            Spacer(Modifier.height(5.dp))
//            Text(
//                text = "Сфотографируйте и поставьте\nотметку - так вы поможете в поисках ",
//                fontSize = 12.sp,
//                color = Color.White
//            )
//        }
//
//        Image(
//            modifier = Modifier
//                .align(Alignment.BottomEnd),
//            painter = painterResource(R.drawable.ic_lenta),
//            contentDescription = null
//        )
//
//
//        Box(
//            modifier = Modifier
//                .padding(end = 10.dp)
//                .size(28.dp)
//                .clip(CircleShape)
//                .background(color = Color.Black)
//                .clickable {
//                    onClick()
//                }
//                .align(Alignment.CenterEnd),
//            contentAlignment = Alignment.Center
//        ) {
//            Image(
//                painter = painterResource(R.drawable.right_arrow),
//                contentDescription = "Загрузить животное",
//                colorFilter = ColorFilter.tint(Color.White),
//            )
//        }
//    }
//}


@Composable
fun StorySectionComponent(
    modifier: Modifier = Modifier,
    storyInfoList: List<StoryInfo>,
    navigateToStoryScreen: (StoryId) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
    ) {

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.Start
            ),
        ) {
            items(storyInfoList) { storyInfo ->
                StoryPreviewComponent(
                    storyInfo = storyInfo,
                    onClick = { navigateToStoryScreen(storyInfo.id) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ArticleSectionComponentPreview(

) {
    StorySectionComponent(
        storyInfoList = storyInfoList,
        navigateToStoryScreen = {}
    )
}


@Composable
fun StoryPreviewComponent(
    modifier: Modifier = Modifier,
    storyInfo: StoryInfo,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            }
            .height(120.dp)
            .width(96.dp)

    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(storyInfo.previewImage),
            contentScale = ContentScale.Crop,
            contentDescription = storyInfo.previewTitle
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.72f)
                        ),
                        startY = 35f
                    )
                )
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            text = storyInfo.previewTitle,
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 13.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TodayMissingPetsSection(
    modifier: Modifier = Modifier,
    pets: LazyPagingItems<PetUiPreview>,
    goToDetailsPetScreen: (String, Int) -> Unit
) {
    val refreshState = pets.loadState.refresh
    val shouldShowSection = refreshState is LoadState.Loading ||
            (refreshState is LoadState.NotLoading && pets.itemCount > 0)

    if (!shouldShowSection) return

    val cardWidth = (LocalConfiguration.current.screenWidthDp.dp - 44.dp) / 2

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Потерялись сегодня",
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))

            when (refreshState) {
                is LoadState.Loading -> {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(TODAY_MISSING_SHIMMER_COUNT) {
                            SearchPetCardShimmerPlaceholder(
                                modifier = Modifier.width(cardWidth)
                            )
                        }
                    }
                }

                is LoadState.NotLoading -> {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            count = pets.itemCount,
                            key = { index -> pets.peek(index)?.id ?: index }
                        ) { index ->
                            val pet = pets[index] ?: return@items

                            PetCardComponent(
                                modifier = Modifier.width(cardWidth),
                                petInfo = pet,
                                isMissing = true
                            ) {
                                goToDetailsPetScreen(pet.id, MISSING_ANNOUNCEMENT_TYPE)
                            }
                        }
                    }
                }

                is LoadState.Error -> Unit
            }
        }
    }
}

@Composable
fun TargetPetsSection(
    modifier: Modifier = Modifier,
    latestStreetPetState: LatestStreetPetState,
    navigateToStreetPetScreen: () -> Unit,
    navigateToCameraScreen: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Замеченные питомцы",
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))
            LatestStreetPetCard(
                state = latestStreetPetState,
                onClick = navigateToStreetPetScreen
            )
            Spacer(Modifier.height(16.dp))
            DefaultButton(
                text = stringResource(R.string.find_animal),
                onClick = {
                    getPhoto(context)
                    navigateToCameraScreen()
                },
                enabled = true,
                containerColor = buttonPrimary
            )
        }
    }
}

@Composable
private fun LatestStreetPetCard(
    state: LatestStreetPetState,
    onClick: () -> Unit
) {
    when (state) {
        is LatestStreetPetState.Content -> {
            NearPetCardComponent(
                Modifier,
                streetPetPreviewModel = state.streetPet
            ) {
                onClick()
            }
        }

        LatestStreetPetState.Loading -> {
            LatestStreetPetCardShimmer()
        }

        LatestStreetPetState.Placeholder -> {
            LatestStreetPetPlaceholderCard(onClick = onClick)
        }
    }
}

@Composable
private fun LatestStreetPetPlaceholderCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
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
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                modifier = Modifier
                    .height(332.dp)
                    .fillMaxWidth()
                    .background(backgroundColor),
                painter = painterResource(R.drawable.ic_dog),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 20.dp)
            ) {
                Row {
                    PetInfoComponent(text = "Пока нет объявлений")
                }
                Spacer(Modifier.height(8.dp))
                PetInfoComponent(text = "Заметьте питомца первым")
            }
        }
    }
}

@Composable
private fun LatestStreetPetCardShimmer(
    modifier: Modifier = Modifier
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            ShimmerImagePlaceholder(
                modifier = Modifier
                    .height(332.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 20.dp)
            ) {
                Row {
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(36.dp)
                            .width(112.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(14.dp))
                    ShimmerTextPlaceholder(
                        modifier = Modifier
                            .height(36.dp)
                            .width(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
                Spacer(Modifier.height(8.dp))
                ShimmerTextPlaceholder(
                    modifier = Modifier
                        .height(36.dp)
                        .width(240.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

private fun getPhoto(context: Context) {
    val cameraPermission = Manifest.permission.CAMERA
    val isGranted = ContextCompat.checkSelfPermission(
        context,
        cameraPermission
    ) == PackageManager.PERMISSION_GRANTED

    if (isGranted) {
        Log.d("CAMERA_X", "permission is granted")
    } else {
        ActivityCompat.requestPermissions(context as Activity, arrayOf(cameraPermission), 0)
    }
}

private const val MISSING_ANNOUNCEMENT_TYPE = 1
private const val TODAY_MISSING_SHIMMER_COUNT = 2

@Preview
@Composable
fun ArticleComponentPreview() {
    StoryPreviewComponent(
        storyInfo = storyInfoList.first(),
        onClick = {

        }
    )
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
