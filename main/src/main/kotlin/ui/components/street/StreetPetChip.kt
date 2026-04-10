package ui.components.street

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.streetPetChipColor

@Composable
fun StreetPetChip(
    modifier: Modifier = Modifier,
    information: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(color = streetPetChipColor)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = information,
            fontSize = 10.sp
        )
    }
}

@Preview
@Composable
private fun StreetPetChipPreview() {
    StreetPetChip(information = "20 минут назад")
}