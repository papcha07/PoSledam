package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.models.FoundPetInfo

@Composable
fun PetScreenDataComponent(
    modifier: Modifier = Modifier,
    petInfo: FoundPetInfo
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row {

            }
        }
    }
}
