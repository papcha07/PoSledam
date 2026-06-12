package ui.components.placeholder

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.core.R

@Composable
fun ShimmerAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    @DrawableRes errorRes: Int = R.drawable.ic_dog,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null
) {
    val hasModel = model.hasImageModel()
    var isImageLoading by remember(model) {
        mutableStateOf(hasModel)
    }

    Box(modifier = modifier) {
        if (hasModel) {
            AsyncImage(
                modifier = Modifier.matchParentSize(),
                model = model,
                error = painterResource(errorRes),
                contentScale = contentScale,
                contentDescription = contentDescription,
                onLoading = {
                    isImageLoading = true
                },
                onSuccess = {
                    isImageLoading = false
                },
                onError = {
                    isImageLoading = false
                    onError?.invoke(it)
                }
            )

            if (isImageLoading) {
                ShimmerImagePlaceholder(
                    modifier = Modifier.matchParentSize(),
                    shape = RectangleShape
                )
            }
        } else {
            Image(
                modifier = Modifier.matchParentSize(),
                painter = painterResource(errorRes),
                contentScale = contentScale,
                contentDescription = contentDescription
            )
        }
    }
}

private fun Any?.hasImageModel(): Boolean {
    return when (this) {
        null -> false
        is String -> isNotBlank()
        else -> true
    }
}
