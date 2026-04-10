package ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.Ser

@Composable
fun PetParameterComponent(
    modifier: Modifier = Modifier,
    type: String,
    value: String
) {
    Row {
        Text(
            text = "${type}:",
            fontSize = 14.sp,
            color = Ser
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = value.toLowerCase(),
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
private fun PetParameterComponentPreview() {
    PetParameterComponent(
        type = "Порода",
        value = "Британец"
    )
}