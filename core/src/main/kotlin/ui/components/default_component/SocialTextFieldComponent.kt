package ui.components.default_component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.registerEditTextColor
import ui.theme.textHint

data class TextFieldData(
    val label: String,
    val hint: String
)

@Composable
fun SocialTextFieldComponent(
    value: String,
    textFieldData: TextFieldData,
    onValueChange: (String) -> Unit,
    @DrawableRes icon: Int
) {

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        leadingIcon = {
            Image(painter = painterResource(icon), "")
        },
        placeholder = {
            Text(
                text = textFieldData.hint,
                color = textHint,
                fontSize = 14.sp
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