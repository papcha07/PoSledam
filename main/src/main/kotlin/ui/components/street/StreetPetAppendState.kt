package ui.components.street

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import ui.components.placeholder.ErrorPlaceholder
import ui.components.placeholder.ShimmerLoadingTransition

@Composable
fun StreetPetAppendState(
    appendState: LoadState,
    modifier: Modifier = Modifier
) {
    ShimmerLoadingTransition(
        isLoading = appendState is LoadState.Loading,
        modifier = modifier,
        loadingContent = {
            StreetPetAppendShimmerPlaceholder()
        }
    ) {
        when (appendState) {
            is LoadState.Loading -> Unit

            is LoadState.Error -> {
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center
                ) {
                    ErrorPlaceholder()
                }
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
