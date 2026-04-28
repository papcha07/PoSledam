package ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.buttonPrimary

@Composable
fun AuthButton(
    modifier: Modifier = Modifier,
    color: Color,
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .height(54.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        onClick = onClick
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp
        )
    }
}


@Preview
@Composable
private fun AuthButtonPreview() {
    AuthButton(
        color = buttonPrimary,
        text = "Получить код",
        textColor = Color.White
    ) {

    }
}