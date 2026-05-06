package ui.components.street

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import ui.components.placeholder.ErrorPlaceholder

@Composable
fun StreetPetRefreshState(
    refreshState: LoadState,
    isEmpty: Boolean,
    modifier: Modifier = Modifier
) {
    when (refreshState) {
        is LoadState.Loading -> {
            CircularProgressIndicator(
                modifier = modifier
            )
        }

        is LoadState.Error -> {
            ErrorPlaceholder(
                modifier = modifier
            )
        }

        is LoadState.NotLoading -> {
            if (isEmpty) {
                ErrorPlaceholder(
                    modifier = modifier
                )
            }
        }
    }
}