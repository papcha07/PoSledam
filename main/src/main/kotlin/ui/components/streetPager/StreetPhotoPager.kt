package ui.components.streetPager

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun StreetPhotoPager(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
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
        }
    }

}


@Composable
fun StreetPhotoPagerPreview(modifier: Modifier = Modifier) {
    StreetPhotoPager(
        photos = listOf()
    )
}