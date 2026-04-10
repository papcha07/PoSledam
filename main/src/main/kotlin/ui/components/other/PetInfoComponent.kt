package ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PetInfoComponent(
    modifier: Modifier = Modifier,
    text: String
) {
    Box(
        modifier = modifier
            .clip
                (RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Color.Black.copy(alpha = 0.35f)
                )
                .blur(8.dp)
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = text,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 15.sp
        )
    }
}

