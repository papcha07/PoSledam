package ui.components.placeholder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ShimmerLoadingTransition(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.animateContentSize()
    ) {
        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn(tween(CONTENT_FADE_IN_DURATION_MS)),
            exit = fadeOut(tween(CONTENT_FADE_OUT_DURATION_MS))
        ) {
            content()
        }

        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(tween(SHIMMER_FADE_IN_DURATION_MS)),
            exit = fadeOut(tween(SHIMMER_FADE_OUT_DURATION_MS))
        ) {
            loadingContent()
        }
    }
}

private const val CONTENT_FADE_IN_DURATION_MS = 220
private const val CONTENT_FADE_OUT_DURATION_MS = 120
private const val SHIMMER_FADE_IN_DURATION_MS = 120
private const val SHIMMER_FADE_OUT_DURATION_MS = 300
