package ui.components.slider

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.R

@Composable
fun PhotosPager(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
    canAddPhoto: Boolean = true,
    onAddPhotoClick: () -> Unit,
    onRemovePhotoClick: ((Uri) -> Unit)? = null
) {
    val pageCount = photos.size + if (canAddPhoto) 1 else 0
    val pagerState = rememberPagerState { pageCount }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(232.dp)
            .testTag("pager")
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            beyondViewportPageCount = 1
        ) { page ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .testTag(
                        if (page < photos.size) "photo_page_$page" else "add_photo_page"
                    )
            ) {
                if (page < photos.size) {
                    val uri = photos[page]
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (onRemovePhotoClick != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable { onRemovePhotoClick(uri) }
                                    .testTag("remove_photo_button_$page"),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    modifier = Modifier.size(20.dp),
                                    painter = painterResource(R.drawable.ic_cancel_button),
                                    contentDescription = "Удалить фото"
                                )
                            }
                        }
                    }
                } else if (canAddPhoto) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(0xFFF5F5F7))
                            .clickable(onClick = onAddPhotoClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_camera_add),
                                contentDescription = "Добавить фото",
                                modifier = Modifier.padding(10.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Добавить фото питомца",
                                fontSize = 14.sp,
                                color = Color(0xFF8E8E93)
                            )
                        }
                    }
                }
            }
        }

        if (photos.size > 1 && pagerState.currentPage < photos.size) {
            PhotoPagerIndicator(
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
private fun PhotoPagerIndicator(
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
