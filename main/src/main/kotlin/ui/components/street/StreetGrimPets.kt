package ui.components.street

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.models.StreetPetPreviewModel

@Composable
fun StreetGridPets(
    modifier: Modifier = Modifier,
    animalList: List<StreetPetPreviewModel>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = animalList,
            key = { animal -> animal.id }
        ) { animal ->
            StreetPetCardComponent(
                streetPetPreviewModel = animal
            )
        }
    }
}