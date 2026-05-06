package ui.components.street

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import ui.components.placeholder.ErrorPlaceholder

@Composable
fun StreetPetAppendState(
    appendState: LoadState,
    modifier: Modifier = Modifier
) {
    when (appendState) {
        is LoadState.Loading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

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