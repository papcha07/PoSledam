package ui.components.bottom_spotted

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import ui.components.ButtonComponent
import ui.theme.Ser
import ui.theme.buttonPrimary

@Composable
fun SpottedPetConfirmationBottomSheetContent(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
    isSendEnabled: Boolean = photos.isNotEmpty(),
    canAddPhoto: Boolean = true,
    onAddPhotoClick: () -> Unit,
    onPhotoClick: (Uri) -> Unit = {},
    onRemovePhotoClick: (Uri) -> Unit = {},
    onSendClick: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 10.dp, bottom = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = "Подтвердите что нашли питомца",
            fontFamily = lebowskiFont,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E1E1E)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            text = "Прикрепите несколько фотографий\nвладелец проверит их",
            textAlign = TextAlign.Center,
            fontFamily = lebowskiFont,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            color = Ser
        )

        Spacer(Modifier.height(24.dp))

        PhotoSlider(
            photos = photos,
            canAddPhoto = canAddPhoto,
            onAddPhotoClick = onAddPhotoClick,
            onPhotoClick = onPhotoClick,
            onRemovePhotoClick = onRemovePhotoClick
        )

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = "В среднем проверка занимает 2 часа",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp,
            color = Ser
        )

        Spacer(Modifier.height(16.dp))

        ButtonComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            color = buttonPrimary,
            text = "Проверить",
            textColor = Color.White,
            enabled = isSendEnabled,
            radius = 40.dp,
            onClick = onSendClick
        )
    }
}

@Composable
private fun PhotoSlider(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
    canAddPhoto: Boolean,
    onAddPhotoClick: () -> Unit,
    onPhotoClick: (Uri) -> Unit,
    onRemovePhotoClick: (Uri) -> Unit
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(PhotoItemHeight),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (canAddPhoto) {
            item {
                AddPhotoItem(onClick = onAddPhotoClick)
            }
        }

        itemsIndexed(
            items = photos,
            key = { index, photo -> "$index-${photo}" }
        ) { _, photo ->
            PhotoItem(
                uri = photo,
                onClick = { onPhotoClick(photo) },
                onRemoveClick = { onRemovePhotoClick(photo) }
            )
        }
    }
}

@Composable
private fun AddPhotoItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .width(AddPhotoItemWidth)
            .fillMaxHeight()
            .clip(RoundedCornerShape(PhotoItemRadius))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFF210B17),
                shape = RoundedCornerShape(PhotoItemRadius)
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "+",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFF210B17)
        )
    }
}

@Composable
private fun PhotoItem(
    modifier: Modifier = Modifier,
    uri: Uri,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Box(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(PhotoItemRadius))
            .border(
                width = 1.dp,
                color = Color(0xFF210B17),
                shape = RoundedCornerShape(PhotoItemRadius)
            )
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            model = uri,
            contentDescription = "Фото питомца",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_dog),
            error = painterResource(R.drawable.ic_dog)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable(onClick = onRemoveClick),
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

private val AddPhotoItemWidth = 100.dp
private val PhotoItemHeight = 373.dp
private val PhotoItemRadius = 12.dp
internal val lebowskiFont = FontFamily(
    Font(R.font.lebowski_by_pragmatica_regular)
)

@Preview(showBackground = true)
@Composable
private fun SpottedPetConfirmationBottomSheetContentPreview() {
    SpottedPetConfirmationBottomSheetContent(
        photos = emptyList(),
        onAddPhotoClick = {},
        onSendClick = {}
    )
}
