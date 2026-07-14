package ui.components.default_component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ButtonPrimary = Color(0xFF210B17)

@Composable
fun DefaultButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = ButtonPrimary,
    textColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            color = textColor,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun DefaultButtonPreview() {
    DefaultButton(
        onClick = {},
        text = "Применить"
    )
}
