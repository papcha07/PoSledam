package ui.screen.camera

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PokemonCatchOverlay(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val overlayAlpha = remember { Animatable(1f) }
    val dimAlpha = remember { Animatable(0f) }
    val ballOffset = remember { Animatable(420f) }
    val ballScale = remember { Animatable(0.4f) }
    val ballRotation = remember { Animatable(0f) }
    val flashProgress = remember { Animatable(0f) }
    val particleProgress = remember { Animatable(0f) }
    val glowProgress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textScale = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        launch { dimAlpha.animateTo(0.6f, tween(durationMillis = 150)) }
        launch {
            ballOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow)
            )
        }
        launch {
            ballScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow)
            )
        }
        launch {
            ballRotation.animateTo(
                targetValue = 720f,
                animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow)
            )
        }

        delay(550)
        listOf(-14f, 12f, -8f, 0f).forEach { angle ->
            ballRotation.animateTo(
                targetValue = angle,
                animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing)
            )
        }

        delay(40)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        launch { flashProgress.animateTo(1f, tween(durationMillis = 600, easing = FastOutSlowInEasing)) }
        launch { particleProgress.animateTo(1f, tween(durationMillis = 650, easing = FastOutSlowInEasing)) }
        launch { glowProgress.animateTo(1f, tween(durationMillis = 180, easing = FastOutSlowInEasing)) }
        launch { textAlpha.animateTo(1f, tween(durationMillis = 180)) }
        launch {
            textScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow)
            )
        }

        delay(720)
        overlayAlpha.animateTo(0f, tween(durationMillis = 250))
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(overlayAlpha.value)
            .background(Color.Black.copy(alpha = dimAlpha.value))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        CatchMomentEffects(
            flashProgress = flashProgress.value,
            particleProgress = particleProgress.value
        )

        PokeballCanvas(
            modifier = Modifier
                .size(148.dp)
                .offset(y = ballOffset.value.dp)
                .graphicsLayer {
                    scaleX = ballScale.value
                    scaleY = ballScale.value
                    rotationZ = ballRotation.value
                },
            glow = glowProgress.value
        )

        Text(
            modifier = Modifier
                .offset(y = 132.dp)
                .graphicsLayer {
                    scaleX = textScale.value
                    scaleY = textScale.value
                }
                .alpha(textAlpha.value),
            text = "Вы поймали питомца!",
            color = Color.White,
            fontFamily = LebowskiByPragmatica,
            fontSize = 26.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun CatchMomentEffects(
    flashProgress: Float,
    particleProgress: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)

        if (flashProgress > 0f) {
            val radius = 48.dp.toPx() * (0.1f + 3.1f * flashProgress)
            drawCircle(
                color = Color.White.copy(alpha = 0.85f * (1f - flashProgress)),
                radius = radius,
                center = center
            )
        }

        if (particleProgress > 0f) {
            val easedProgress = 1f - (1f - particleProgress) * (1f - particleProgress)
            val distance = 130.dp.toPx() * easedProgress
            val particleRadius = 6.dp.toPx() * (1f - particleProgress * 0.45f)
            repeat(PARTICLE_COUNT) { index ->
                val angle = -PI / 2.0 + (2.0 * PI * index / PARTICLE_COUNT)
                val particleCenter = Offset(
                    x = center.x + cos(angle).toFloat() * distance,
                    y = center.y + sin(angle).toFloat() * distance
                )
                drawCircle(
                    color = if (index % 2 == 0) {
                        Color.White.copy(alpha = 1f - particleProgress)
                    } else {
                        Color(0xFFF4C542).copy(alpha = 1f - particleProgress)
                    },
                    radius = particleRadius,
                    center = particleCenter
                )
            }
        }
    }
}

@Composable
private fun PokeballCanvas(
    modifier: Modifier = Modifier,
    glow: Float
) {
    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val left = (size.width - diameter) / 2f
        val top = (size.height - diameter) / 2f
        val ballSize = Size(diameter, diameter)
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = diameter / 2f
        val outlineWidth = diameter * 0.045f
        val stripeHeight = diameter * 0.09f

        if (glow > 0f) {
            drawCircle(
                color = Color(0xFFF4C542).copy(alpha = 0.24f * glow),
                radius = diameter * 0.64f,
                center = center
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.14f * glow),
                radius = diameter * 0.52f,
                center = center
            )
        }

        drawArc(
            color = Color(0xFFE3350D),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(left, top),
            size = ballSize
        )
        drawArc(
            color = Color.White,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(left, top),
            size = ballSize
        )
        drawRect(
            color = Color.Black,
            topLeft = Offset(left, center.y - stripeHeight / 2f),
            size = Size(diameter, stripeHeight)
        )
        drawCircle(
            color = Color.Black,
            radius = diameter * 0.15f,
            center = center
        )
        drawCircle(
            color = Color.White,
            radius = diameter * 0.105f,
            center = center
        )
        drawCircle(
            color = Color(0xFFBDBDC5),
            radius = diameter * 0.105f,
            center = center,
            style = Stroke(width = diameter * 0.018f)
        )
        drawCircle(
            color = Color.Black,
            radius = radius - outlineWidth / 2f,
            center = center,
            style = Stroke(width = outlineWidth)
        )
    }
}

private const val PARTICLE_COUNT = 10

private val LebowskiByPragmatica = FontFamily(
    Font(R.font.lebowski_by_pragmatica_regular)
)
