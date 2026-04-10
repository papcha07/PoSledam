package ui.components.default_component

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.model.TabRowInfo

@Composable
fun TabRowSelection(
    modifier: Modifier = Modifier,
    listOfTabInfo: List<TabRowInfo>,
    selectedTabIndex: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    TabRow(
        modifier = modifier
            .testTag("tab_row")
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(34.dp)),
        selectedTabIndex = selectedTabIndex,
        containerColor = Color(0xFFF8F9FE),
        indicator = {},
        divider = {}
    ) {
        listOfTabInfo.forEachIndexed { index, tabRowInfo ->
            val isSelected = index == selectedTabIndex
            val backgroundAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(durationMillis = 580),
                label = "tabBackgroundAlpha"
            )
            Tab(
                modifier = Modifier
                    .testTag("tab_${tabRowInfo.name}")
                    .semantics { selected = isSelected },
                selected = index == selectedTabIndex,
                onClick = {
                    onTabSelected(index)
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp)
                            .clip(RoundedCornerShape(34.dp))
                            .background(Color.White.copy(alpha = backgroundAlpha)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabRowInfo.name,
                            color = Color.Black,
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun TabRowPreview() {
    TabRowSelection(
        listOfTabInfo = listOf(
            TabRowInfo("Найденные"),
            TabRowInfo("Пропажи")
        ),
        selectedTabIndex = 0,
        onTabSelected = { selectedIndex ->
            // Обработка выбора таба будет в родительском компоненте
        }
    )
}