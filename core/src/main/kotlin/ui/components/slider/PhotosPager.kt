package ui.components.slider

import android.net.Uri
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
    onAddPhotoClick: () -> Unit,
    onRemovePhotoClick: ((Uri) -> Unit)? = null
) {
    val pageCount = photos.size + 1
    val pagerState = rememberPagerState { pageCount }

    HorizontalPager(
        modifier = modifier.testTag("pager"),
        state = pagerState,
        beyondViewportPageCount = 1
    ) { page ->

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(232.dp)
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
                                .clickable { onRemovePhotoClick(uri) }
                        ) {
                            // Иконку крестика можно добавить позже, сейчас это прозрачная зона клика
                        }
                    }
                }
            } else {
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
}
