package ui.components.placeholder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.components.default_component.DefaultButton

@Composable
fun SuccessSendPlaceholder(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    leftFromAnnouncement: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 16.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.send_placeholder),
                contentDescription = null
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            DefaultButton(
                modifier = Modifier.padding(16.dp),
                onClick = leftFromAnnouncement,
                text = "МЯУ"
            )
        }
    }
}

@Composable
fun SuccessSendPopup(
    visible: Boolean,
    title: String,
    description: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(220)) +
                        scaleIn(
                            initialScale = 0.88f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) +
                        slideInVertically(
                            initialOffsetY = { it / 5 },
                            animationSpec = tween(220)
                        ),
                exit = fadeOut(animationSpec = tween(140)) +
                        scaleOut(
                            targetScale = 0.92f,
                            animationSpec = tween(140)
                        ) +
                        slideOutVertically(
                            targetOffsetY = { it / 8 },
                            animationSpec = tween(140)
                        )
            ) {
                SuccessSendPlaceholder(
                    title = title,
                    description = description,
                    leftFromAnnouncement = onDismiss
                )
            }
        }
    }
}


@Preview
@Composable
private fun SuccessSendPlaceholderPreview() {
    SuccessSendPlaceholder(
        title = "Объявление подано",
        description = "Включите уведомления чтобы не пропустить важную информацию о вашем питомце"
    ) {

    }
}