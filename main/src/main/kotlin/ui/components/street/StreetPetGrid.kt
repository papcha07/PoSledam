package ui.components.street

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import domain.models.StreetPetPreviewModel
import ui.components.other.NearPetCardComponent

@Composable
fun StreetPetGrid(
    animals: LazyPagingItems<StreetPetPreviewModel>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier,
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val animalCount = animals.itemCount
            if (animalCount > 0) {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    val firstAnimal = animals[0]
                    if (firstAnimal != null) {
                        NearPetCardComponent(
                            streetPetPreviewModel = firstAnimal,
                            navigateToStreetPetScreen = {

                            }
                        )
                    }
                }
            }

            items(
                count = maxOf(animalCount - 1, 0)
            ) { index ->
                val animal = animals[index + 1]
                if (animal != null) {
                    StreetPetCardComponent(
                        streetPetPreviewModel = animal
                    )
                }
            }

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                StreetPetAppendState(
                    appendState = animals.loadState.append,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}