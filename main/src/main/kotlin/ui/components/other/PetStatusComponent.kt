package ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.StatusPetBackground

@Composable
fun PetStatusComponent(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier
            .background(
                color = StatusPetBackground,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(all = 8.dp),
        color = Color.White,
        text = text,
        fontSize = 12.sp,
    )
}
