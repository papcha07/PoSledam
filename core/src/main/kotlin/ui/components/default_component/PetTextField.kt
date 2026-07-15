package ui.components.default_component

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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.BrushColor
import ui.theme.GrayEditTextColor
import ui.theme.deleteButtonColor

@Composable
fun PetTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    maxLines: Int? = null,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
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
            .clip(RoundedCornerShape(14.dp)),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                color = GrayEditTextColor
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        maxLines = maxLines ?: 1
    )


}

