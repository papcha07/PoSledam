package ui.components.default_component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R

data class ToolBarInfo(
    val title: String? = null,
    val backArrow: Boolean = false,
    val nextRoute: String? = null,
    val prevRoute: String? = null,
    @DrawableRes val backArrowIcon: Int? = null,
    @DrawableRes val actionIcon: Int? = null,
    val onBackClick: (() -> Unit)? = null,
    val onActionClick: (() -> Unit)? = null
)

@Composable
fun ToolBar(
    modifier: Modifier = Modifier,
    toolBarInfo: ToolBarInfo,
    onBackClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {

        if (toolBarInfo.backArrow) {
            IconButton(
                onClick = {
                    toolBarInfo.onBackClick?.invoke() ?: onBackClick()
                },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(
                        toolBarInfo.backArrowIcon ?: R.drawable.left_arrow
                    ),
                    contentDescription = "Назад"
                )
            }
        }

        if (toolBarInfo.title != null) {
            Text(
                text = toolBarInfo.title,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        toolBarInfo.actionIcon?.let { iconRes ->
            IconButton(
                onClick = {
                    toolBarInfo.onActionClick?.invoke() ?: onActionClick()
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = "Действие"
                )
            }
        }
    }
}


@Preview
@Composable
private fun ToolBarPreview() {
    ToolBar(
        toolBarInfo = ToolBarInfo(
            title = "Поиск потерянных",
            backArrow = true,
            nextRoute = "",
            prevRoute = "",
            backArrowIcon = R.drawable.left_arrow,
            actionIcon = R.drawable.ic_settings
        )
    )
}