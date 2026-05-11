package ui.components.streetPager

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.R

@Composable
fun StreetPhotoPager(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
    returnBack: () -> Unit
) {
    val pageCount = photos.size
    val pagerState = rememberPagerState { pageCount }
    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1
    ) { page ->
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
        ) {


            val currentUri = photos[page]
            AsyncImage(
                model = currentUri,
                contentDescription = "animal photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 30.dp, start = 30.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable {
                        returnBack()
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.street_back_arrow),
                    contentDescription = "back button",
                    tint = Color.Unspecified

                )
            }

        }
    }

}


@Composable
fun StreetPhotoPagerPreview(modifier: Modifier = Modifier) {
    StreetPhotoPager(
        photos = listOf(),
        returnBack = {

        }
    )
}