package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpOffset
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
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
) {

    TextField(
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
            )
            .fillMaxWidth() ,
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
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        placeholder = {
            Text(
                text = textFieldData.hint,
                color = textHint
            )
        },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = Color.Black,
            focusedPlaceholderColor = Color.Black
        )
    )
}


