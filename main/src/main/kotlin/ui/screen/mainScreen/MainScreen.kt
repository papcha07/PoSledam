package ui.screen.mainScreen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.R
import domain.models.StreetPetPreviewModel
import helper.RequestLocationPermission
import ui.components.other.NearPetCardComponent
import ui.components.profilebar.ProfileBarComponent
import ui.model.ArticleInfo
import ui.theme.backgroundColor

val articleInfoList = listOf(
    ArticleInfo(
        image = R.drawable.f_article
    ),
    ArticleInfo(
        image = R.drawable.s_article
    ),
    ArticleInfo(
        image = R.drawable.t_article
    ),
    ArticleInfo(
        image = R.drawable.f_article
    ),
    ArticleInfo(
        image = R.drawable.s_article
    ),
    ArticleInfo(
        image = R.drawable.t_article
    )
)

@Composable
fun MainScreen(
    navigateToNotificationScreen: () -> Unit,
    navigateToStreetPetScreen: () -> Unit,
    navigateToCameraScreen: () -> Unit,
    navigateToNewsScreen: () -> Unit,
    mainScreenViewModel: MainScreenViewModel,
) {


    val userInfoState by mainScreenViewModel.userInfoState.collectAsStateWithLifecycle()
    val markIsReadState by mainScreenViewModel.markNotificationState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        mainScreenViewModel.observeUser()
        mainScreenViewModel.refreshUser()
    }

    var locationEnabled by remember { mutableStateOf(false) }
    var locationWasUpdated by remember { mutableStateOf(false) }

    RequestLocationPermission(
        onPermissionGranted = {
            locationEnabled = true
        },
        onPermissionDenied = {
            locationEnabled = false
        }
    )

    LaunchedEffect(locationEnabled) {
        if (locationEnabled) {
            mainScreenViewModel.startLocationWorker()
        }
    }

    LaunchedEffect(locationEnabled) {
        if (locationEnabled && !locationWasUpdated) {
            mainScreenViewModel.updateUserLocation()
            locationWasUpdated = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        item {
            ProfileBarComponent(
                profileBarState = userInfoState,
                onSettingsClick = {},
                notificationsIsNotRead = markIsReadState,
                onNotifyClick = {
                    navigateToNotificationScreen()
                }

            )
        }
        item {
            ArticleSectionComponent(
                modifier = Modifier,
                articleInfoList = articleInfoList,
                navigateToNewsScreen = navigateToNewsScreen
            )
        }
        item {
            Spacer(Modifier.height(4.dp))
        }

        item {
            TargetPetsSection(
                navigateToStreetPetScreen = navigateToStreetPetScreen,
                navigateToCameraScreen = navigateToCameraScreen
            )
        }
    }
}

@Composable
fun GetPhotoComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4F52EC), Color(0xFF97BBFF)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
    ) {

        Column(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp)
        ) {

            Text(
                text = "Замечен!",
                fontSize = 14.sp,
                color = Color.White
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Сфотографируйте и поставьте\nотметку - так вы поможете в поисках ",
                fontSize = 12.sp,
                color = Color.White
            )
        }

        Image(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            painter = painterResource(R.drawable.ic_lenta),
            contentDescription = null
        )


        Box(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(color = Color.Black)
                .clickable {
                    onClick()
                }
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.right_arrow),
                contentDescription = "Загрузить животное",
                colorFilter = ColorFilter.tint(Color.White),
            )
        }
    }
}


@Composable
fun ArticleSectionComponent(
    modifier: Modifier = Modifier,
    articleInfoList: List<ArticleInfo>,
    navigateToNewsScreen: () -> Unit
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
            Modifier.padding(top = 16.dp, start = 16.dp, bottom = 16.dp)
        ) {
            items(articleInfoList) { articleInfo ->
                ArticleComponent(
                    articleInfo = articleInfo,
                    onClick = navigateToNewsScreen
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

@Preview
@Composable
private fun ArticleSectionComponentPreview(

) {
    ArticleSectionComponent(
        articleInfoList = articleInfoList,
        navigateToNewsScreen = {}
    )
}


@Composable
fun ArticleComponent(
    modifier: Modifier = Modifier,
    articleInfo: ArticleInfo,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            }
            .height(100.dp)
            .width(90.dp)

    ) {
        Image(
            modifier = Modifier.align(Alignment.BottomStart),
            painter = painterResource(articleInfo.image),
            contentDescription = "Статья"
        )
    }
}

@Composable
fun TargetPetsSection(
    modifier: Modifier = Modifier,
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
            GetPhotoComponent(
                onClick = {
                    getPhoto(context)
                    navigateToCameraScreen()
                }
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = "Замеченные питомцы",
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))
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
                navigateToStreetPetScreen()
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

@Preview
@Composable
fun ArticleComponentPreview() {
    ArticleComponent(
        articleInfo = ArticleInfo(
            image = R.drawable.f_article
        ),
        onClick = {

        }
    )
}


@Preview
@Composable
fun GetPhotoComponentPreview() {
    GetPhotoComponent(Modifier) {

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




