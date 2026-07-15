package ui.components.announcement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.theme.eventDateComponentColor
import ui.theme.textHint

@Composable
fun EventDateComponent(
    modifier: Modifier = Modifier,
    advertState: String
) {
    Box(
        modifier = modifier
            .dropShadow(
                shape = RoundedCornerShape(24.dp),
                shadow = Shadow(
                    radius = 5.5.dp,
                    spread = 0.dp,
                    color = Color(0x0A292929),
                    offset = DpOffset(0.dp, 1.dp)
                )
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                Color.White,
                RoundedCornerShape(14.dp)
            )
            .border(
                width = 0.2.dp,
                color =
                    Color(0xFFE8E8E8),
                shape = RoundedCornerShape(14.dp)
            )            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.ic_calendar_component),
                contentDescription = "calendar"
            )
            Spacer(
                modifier = Modifier.width(5.dp)
            )
            Column(Modifier.padding(10.dp)) {
                Text(
                    text = "Когда нашли",
                    color = textHint,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = advertState,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun EventDateComponentPreview() {
    EventDateComponent(
        advertState = "29/02 12:41"
    )
}