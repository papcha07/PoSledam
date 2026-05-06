package ui.components.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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


@Preview
@Composable
private fun SuccessSendPlaceholderPreview() {
    SuccessSendPlaceholder(
        title = "Объявление подано",
        description = "Включите уведомления чтобы не пропустить важную информацию о вашем питомце"
    ) {

    }
}