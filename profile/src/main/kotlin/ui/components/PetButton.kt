package ui.components

import androidx.compose.foundation.layout.height
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
import ui.theme.buttonPrimary


@Composable
fun PetButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .clip(RoundedCornerShape(40.dp))
            .height(46.dp),
        onClick = onClick,
        shape = RoundedCornerShape(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonPrimary,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Добавить объявление"
        )
    }
}

@Preview
@Composable
private fun PetButtonPreview() {

    PetButton(
        onClick = {

        }
    )
}