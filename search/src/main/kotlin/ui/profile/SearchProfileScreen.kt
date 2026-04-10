package ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.theme.backgroundColor

@Composable
fun SearchProfileScreen(
    modifier: Modifier = Modifier,
    personDto: PersonDto
) {
    Column(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        ToolBar(
            toolBarInfo = ToolBarInfo(
                title = "Профиль",
            )
        )
        Spacer(Modifier.height(8.dp))
        PersonCardComponent(
            personDto = personDto
        )
    }
}