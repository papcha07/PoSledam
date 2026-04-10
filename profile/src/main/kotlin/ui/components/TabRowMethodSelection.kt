package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ui.theme.PurpleButtonColor

@Composable
fun TabRowMethodSelection(
    modifier: Modifier = Modifier,
    listOfTabInfo: List<TabRowInfo>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        modifier = modifier
            .testTag("method_tab_row")
            .fillMaxWidth(),
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        indicator = {},
        divider = {}
    ) {
        Row(Modifier.fillMaxWidth()) {
            listOfTabInfo.forEachIndexed { index, tabRowInfo ->
                val isSelected = index == selectedTabIndex

                Tab(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .weight(1f)
                        .testTag("tab_${tabRowInfo.name}")
                        .semantics { selected = isSelected }
                    ,
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(32.dp))
                                .background(
                                    if (isSelected) PurpleButtonColor
                                    else Color(0xFFF1F2F2)
                                )
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabRowInfo.name,
                                color = if (isSelected) Color.White else Color.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }
        }
    }
}
