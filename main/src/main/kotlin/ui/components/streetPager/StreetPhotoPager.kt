package ui.components.streetPager

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun StreetPhotoPager(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
) {
    val pageCount = photos.size
    val pagerState = rememberPagerState { pageCount }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            beyondViewportPageCount = 1
        ) { page ->
            val currentUri = photos[page]
            AsyncImage(
                model = currentUri,
                contentDescription = "animal photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (photos.size > 1) {
            StreetPhotoPagerIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                currentIndex = pagerState.currentPage,
                totalCount = photos.size
            )
        }
    }

}

@Composable
private fun StreetPhotoPagerIndicator(
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


@Composable
fun StreetPhotoPagerPreview(modifier: Modifier = Modifier) {
    StreetPhotoPager(
        photos = listOf()
    )
}
