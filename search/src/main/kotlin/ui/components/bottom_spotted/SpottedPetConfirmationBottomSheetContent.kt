package ui.components.bottom_spotted

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import ui.theme.buttonSecondPrimary

@Composable
fun SpottedPetConfirmationBottomSheetContent(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
    isSendEnabled: Boolean = photos.isNotEmpty(),
    onAddPhotoClick: () -> Unit,
    onPhotoClick: (Uri) -> Unit = {},
    onSendClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA))
            .padding(top = 10.dp, bottom = 20.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = "Подтвердите, что вы нашли питомца",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E1E1E)
        )

        Spacer(Modifier.height(14.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            text = "Добавьте его фотографии из устройства",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            color = Ser
        )

        Spacer(Modifier.height(34.dp))

        PhotoSlider(
            photos = photos,
            onAddPhotoClick = onAddPhotoClick,
            onPhotoClick = onPhotoClick
        )

        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = "Владелец проверит фотографии",
            fontSize = 18.sp,
            color = Ser
        )

        Spacer(Modifier.height(24.dp))

        ButtonComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            color = buttonPrimary,
            text = "Отправить владельцу",
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
    onAddPhotoClick: () -> Unit,
    onPhotoClick: (Uri) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AddPhotoItem(onClick = onAddPhotoClick)
        }

        itemsIndexed(
            items = photos,
            key = { index, photo -> "$index-${photo}" }
        ) { _, photo ->
            PhotoItem(
                uri = photo,
                onClick = { onPhotoClick(photo) }
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
            .size(PhotoItemSize)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = buttonSecondPrimary,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            fontSize = 40.sp,
            fontWeight = FontWeight.Light,
            color = buttonSecondPrimary
        )
    }
}

@Composable
private fun PhotoItem(
    modifier: Modifier = Modifier,
    uri: Uri,
    onClick: () -> Unit
) {
    AsyncImage(
        modifier = modifier
            .size(PhotoItemSize)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                color = buttonSecondPrimary,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        model = uri,
        contentDescription = "Фото питомца",
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.ic_dog),
        error = painterResource(R.drawable.ic_dog)
    )
}

private val PhotoItemSize = 156.dp

@Preview(showBackground = true)
@Composable
private fun SpottedPetConfirmationBottomSheetContentPreview() {
    SpottedPetConfirmationBottomSheetContent(
        photos = emptyList(),
        onAddPhotoClick = {},
        onSendClick = {}
    )
}
