package ui.components.profilebar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.core.R
import ui.BASE_URL
import ui.components.placeholder.ShimmerImagePlaceholder

@Composable
fun ImageProfile(
    modifier: Modifier = Modifier,
    profileBarState: ProfileBarState
) {
    when (profileBarState) {
        is ProfileBarState.Failed -> {
            Image(
                painter = painterResource(R.drawable.avatar),
                contentDescription = "Фотография профиля (ошибка)",
                modifier = Modifier
                    .size(40.dp),
                contentScale = ContentScale.Crop
            )
        }

        is ProfileBarState.Idle -> {

        }

        is ProfileBarState.Loading -> {
            ShimmerImagePlaceholder(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }

        is ProfileBarState.Success -> {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${profileBarState.userDataInfo.avatarPath}")
                    .crossfade(true)
                    .build(),
                contentDescription = "Фотография профиля",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    ShimmerImagePlaceholder(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                },
                error = {
                    Image(
                        painter = painterResource(R.drawable.avatar),
                        contentDescription = "Фотография профиля (ошибка)",
                        modifier = Modifier
                            .size(40.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            )
        }
    }
}