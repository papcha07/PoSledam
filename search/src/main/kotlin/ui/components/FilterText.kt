package ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun FilterText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        fontSize = 15.sp,
        color = Color(0xFFA8A8A8)
    )
}