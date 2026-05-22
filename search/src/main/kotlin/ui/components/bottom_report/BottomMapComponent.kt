package ui.components.bottom_report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ui.components.ProfileMap

@Composable
fun BottomMapComponent(
    modifier: Modifier = Modifier,
    updateLongitude: (Double) -> Unit,
    updateLatitude: (Double) -> Unit
) {
    val mapShape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(260.dp)
    ) {
        ProfileMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(mapShape),
            pointClick = { lon, lat ->
                updateLongitude(lon)
                updateLatitude(lat)
            }
        )
    }
}
