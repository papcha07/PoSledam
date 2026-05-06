package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.theme.backgroundColor

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        ToolBar(
            modifier = Modifier,
            toolBarInfo = ToolBarInfo(
                title = "Умный поиск",
                backArrow = true,
            ),
            onBackClick = {
                onBackClick()
            },
        )


    }
}


@Preview
@Composable
private fun ChatScreenPreview() {
    ChatScreen {

    }
}

