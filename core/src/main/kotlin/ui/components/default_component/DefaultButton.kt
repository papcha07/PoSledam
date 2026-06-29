package ui.components.default_component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.buttonPrimary

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    containerColor: Color = buttonPrimary
) {
    Button(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        onClick = onClick
    ) {
        Text(
            text = text,
            fontSize = 16.sp
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
