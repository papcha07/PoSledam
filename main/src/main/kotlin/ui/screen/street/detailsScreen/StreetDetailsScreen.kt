package ui.screen.street.detailsScreen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.components.streetPager.StreetPhotoPager
import ui.screen.street.StreetPetViewModel
import ui.theme.backgroundColor


@Composable
fun StreetPetDetailRouter(
    modifier: Modifier = Modifier,
    streetPetViewModel: StreetPetViewModel,
    animalId: String
) {
    StreetDetailsScreen(
        photos = TODO(),
        returnBack = TODO()
    )
}

@Composable
fun StreetDetailsScreen(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
    returnBack: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .background(color = backgroundColor)
                .fillMaxSize()
        ) {
            StreetPhotoPager(
                photos = photos,
                returnBack = returnBack
            )
            Spacer(Modifier.height(4.dp))
        }

    }
}

@Preview
@Composable
private fun StreetDetailsScreenPreview() {
    StreetDetailsScreen(
        photos = listOf(),
        returnBack = {
            
        }
    )
}