package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.core.R
import ui.BASE_URL
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.model.UserDataUiInfo


@Composable
fun ProfileSectionComponent(
    modifier: Modifier = Modifier,
    profileUiState: UserDataUiInfo,
    navigateToSettingsScreen: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                navigateToSettingsScreen()
            }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("$BASE_URL/api/image/${profileUiState.uri}")
                    .crossfade(true)
                    .build(),
                contentDescription = "Фотография профиля",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    ShimmerImagePlaceholder(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape),
                    )
                },
                error = {
                    Image(
                        painter = painterResource(R.drawable.purple_dog_article),
                        contentDescription = "Фотография профиля (по умолчанию)",
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            )
            Spacer(Modifier.width(14.dp))
            Column {


                Text(
                    text = profileUiState.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = profileUiState.description,
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

            }
            Spacer(Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                    }
                    .padding(end = 16.dp),
                painter = painterResource(R.drawable.right_arrow),
                contentDescription = "Перейти в профиль"
            )
        }
    }
}
