import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.model.StoryId
import ui.model.StoryScrimStyle
import ui.model.StorySlide
import ui.model.StorySlideLayout
import ui.model.findStoryInfo
import ui.theme.Pink80
import ui.theme.buttonSecondPrimary

private val StoryTitleFont = FontFamily(Font(R.font.lebowski_by_pragmatica_regular))
private const val TelegramUrl = "https://t.me/posledamapp"

@Composable
fun StoryScreen(
    modifier: Modifier = Modifier,
    storyId: StoryId,
    goBackClick: () -> Unit
) {
    val storyInfo = findStoryInfo(storyId) ?: return
    var currentSlideIndex by rememberSaveable(storyInfo.id.name) {
        mutableStateOf(0)
    }
    val slides = storyInfo.slides
    val currentSlide = slides[currentSlideIndex.coerceIn(0, slides.lastIndex)]
    val uriHandler = LocalUriHandler.current

    fun openPreviousSlide() {
        if (currentSlideIndex > 0) {
            currentSlideIndex -= 1
        }
    }

    fun openNextSlide() {
        if (currentSlideIndex < slides.lastIndex) {
            currentSlideIndex += 1
        } else {
            goBackClick()
        }
    }

    BackHandler {
        goBackClick()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
    ) {
        StorySlideContent(
            modifier = Modifier.fillMaxSize(),
            slide = currentSlide
        )

        StoryTapZones(
            onPrevious = ::openPreviousSlide,
            onNext = ::openNextSlide
        )

        if (currentSlide.primaryButtonText != null && currentSlide.primaryButtonUrl != null) {
            key(currentSlideIndex) {
                StoryPrimaryActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 28.dp, vertical = 28.dp),
                    text = currentSlide.primaryButtonText,
                    onClick = {
                        uriHandler.openUri(currentSlide.primaryButtonUrl.takeIf { it.isNotBlank() } ?: TelegramUrl)
                    }
                )
            }
        }

        StoryTopControls(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            currentSlideIndex = currentSlideIndex,
            totalSlides = slides.size,
            onCloseClick = goBackClick
        )
    }
}

@Composable
private fun StorySlideContent(
    modifier: Modifier = Modifier,
    slide: StorySlide
) {
    Box(modifier = modifier.background(slide.backgroundColor)) {
        slide.backgroundImage?.let { backgroundImage ->
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(backgroundImage),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }

        if (slide.scrimStyle == StoryScrimStyle.Dark) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.70f),
                                Color.Black.copy(alpha = 0.18f),
                                Color.Black.copy(alpha = 0.84f)
                            )
                        )
                    )
            )
        }

        key(slide) {
            when (slide.layout) {
                StorySlideLayout.Title -> StoryTitleLayout(
                    modifier = Modifier.fillMaxSize(),
                    slide = slide
                )

                StorySlideLayout.Body -> StoryBodyLayout(
                    modifier = Modifier.fillMaxSize(),
                    slide = slide
                )

                StorySlideLayout.Bottom -> StoryBottomLayout(
                    modifier = Modifier.fillMaxSize(),
                    slide = slide
                )

                StorySlideLayout.Centered -> StoryCenteredLayout(
                    modifier = Modifier.fillMaxSize(),
                    slide = slide
                )
            }
        }
    }
}

@Composable
private fun StoryTitleLayout(
    modifier: Modifier = Modifier,
    slide: StorySlide
) {
    Column(
        modifier = modifier.padding(start = 32.dp, top = 112.dp, end = 32.dp)
    ) {
        StoryAnimatedVisibility {
            Text(
                text = slide.title,
                color = slide.textColor,
                fontFamily = StoryTitleFont,
                fontSize = 29.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Normal
            )
        }

        slide.subtitle?.let { subtitle ->
            Spacer(Modifier.height(8.dp))
            StoryAnimatedVisibility(delayMillis = 90) {
                Text(
                    text = subtitle,
                    color = slide.textColor,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StoryBodyLayout(
    modifier: Modifier = Modifier,
    slide: StorySlide
) {
    Box(modifier = modifier.padding(horizontal = 28.dp)) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 116.dp)
        ) {
            StoryAnimatedVisibility {
                Text(
                    text = slide.title,
                    color = slide.textColor,
                    fontFamily = StoryTitleFont,
                    fontSize = 26.sp,
                    lineHeight = 31.sp
                )
            }

            slide.subtitle?.let { subtitle ->
                Spacer(Modifier.height(13.dp))
                StoryAnimatedVisibility(delayMillis = 90) {
                    Text(
                        text = subtitle,
                        color = slide.textColor,
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            slide.body?.let { body ->
                Spacer(Modifier.height(14.dp))
                StoryAnimatedVisibility(delayMillis = 140) {
                    Text(
                        text = body,
                        color = slide.textColor,
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        if (slide.footerTitle != null || slide.footerBody != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = if (slide.primaryButtonText != null) 112.dp else 56.dp)
            ) {
                slide.footerTitle?.let { footerTitle ->
                    StoryAnimatedVisibility(delayMillis = 190) {
                        Text(
                            text = footerTitle,
                            color = slide.textColor,
                            fontFamily = StoryTitleFont,
                            fontSize = 25.sp,
                            lineHeight = 30.sp
                        )
                    }
                }

                slide.footerBody?.let { footerBody ->
                    Spacer(Modifier.height(10.dp))
                    StoryAnimatedVisibility(delayMillis = 230) {
                        Text(
                            text = footerBody,
                            color = slide.textColor,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryBottomLayout(
    modifier: Modifier = Modifier,
    slide: StorySlide
) {
    Box(modifier = modifier.padding(horizontal = 28.dp)) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 72.dp)
        ) {
            StoryAnimatedVisibility {
                Text(
                    text = slide.title,
                    color = slide.textColor,
                    fontFamily = StoryTitleFont,
                    fontSize = 26.sp,
                    lineHeight = 31.sp
                )
            }

            slide.body?.let { body ->
                Spacer(Modifier.height(14.dp))
                StoryAnimatedVisibility(delayMillis = 140) {
                    Text(
                        text = body,
                        color = slide.textColor,
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryCenteredLayout(
    modifier: Modifier = Modifier,
    slide: StorySlide
) {
    Box(
        modifier = modifier.padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        StoryAnimatedVisibility {
            Text(
                text = slide.title,
                color = slide.textColor,
                fontFamily = StoryTitleFont,
                fontSize = 34.sp,
                lineHeight = 39.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StoryPrimaryActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    StoryAnimatedVisibility(
        modifier = modifier,
        delayMillis = 260
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                ),
            onClick = onClick,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonSecondPrimary,
                contentColor = Color(0xFF210B17)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StoryAnimatedVisibility(
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 420,
                delayMillis = delayMillis
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 420,
                delayMillis = delayMillis
            ),
            initialOffsetY = { it / 4 }
        )
    ) {
        content()
    }
}

@Composable
private fun StoryTapZones(
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onPrevious
                )
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onNext
                )
        )
    }
}

@Composable
private fun StoryTopControls(
    modifier: Modifier = Modifier,
    currentSlideIndex: Int,
    totalSlides: Int,
    onCloseClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        StoryProgressBar(
            currentSlideIndex = currentSlideIndex,
            totalSlides = totalSlides
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = onCloseClick
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(R.drawable.ic_close),
                    tint = Color.White,
                    contentDescription = "Закрыть сторис"
                )
            }
        }
    }
}

@Composable
private fun StoryProgressBar(
    currentSlideIndex: Int,
    totalSlides: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(totalSlides) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (index <= currentSlideIndex) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.35f)
                        }
                    )
            )
        }
    }
}
