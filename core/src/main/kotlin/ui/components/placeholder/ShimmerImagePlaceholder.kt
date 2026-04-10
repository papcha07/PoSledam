package ui.components.placeholder

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerImagePlaceholder(
    modifier: Modifier = Modifier
        .size(42.dp)
        .clip(RoundedCornerShape(10.dp))
) {
    var widthPx by remember { mutableStateOf(0f) }

    val transition = rememberInfiniteTransition(label = "image_shimmer")
    val xShimmer by transition.animateFloat(
        initialValue = -widthPx,
        targetValue = widthPx * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "image_shimmer_x"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(xShimmer - widthPx, 0f),
        end = Offset(xShimmer, 0f)
    )

    Box(
        modifier = modifier
            .onGloballyPositioned { widthPx = it.size.width.toFloat() }
            .background(brush, shape = RoundedCornerShape(10.dp))
    )
}