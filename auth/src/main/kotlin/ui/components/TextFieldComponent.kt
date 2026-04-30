package ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.model.data.TextFieldData
import ui.theme.Ser
import ui.theme.registerEditTextColor
import ui.theme.textHint

@Composable
fun TextFieldComponent(
    modifier: Modifier = Modifier,
    value: String,
    textFieldData: TextFieldData,
    onValueChange: (String) -> Unit,
) {

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(
                text = textFieldData.label,
                color = Ser
            )
        },
        placeholder = {
            Text(
                text = textFieldData.hint,
                color = textHint
            )
        },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = registerEditTextColor,
            unfocusedContainerColor = registerEditTextColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = Color.Black,
            focusedPlaceholderColor = Color.Black
        )
    )
}


