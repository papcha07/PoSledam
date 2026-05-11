package ui.screen.street.detailsScreen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.textHint

@Composable
fun DescriptionComponent(
    modifier: Modifier = Modifier,
    placeDescription: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Где был замечен:",
            fontSize = 16.sp
        )
        placeDescription?.let {
            Spacer(Modifier.height(12.dp))
            Text(
                text = placeDescription,
                fontSize = 14.sp,
                color = textHint
            )
        }
    }
}

@Preview
@Composable
private fun DescriptionComponentPreview() {
    DescriptionComponent(
        placeDescription = "Возле дома сидел на дереве"
    )
}