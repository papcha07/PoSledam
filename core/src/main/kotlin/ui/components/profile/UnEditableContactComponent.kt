package ui.components.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.districtDropDownMenuColor

@Composable
fun UnEditableContactComponent(
    modifier: Modifier = Modifier,
    uri: String,
    @DrawableRes icon: Int,
) {
    val clipboardManager = LocalClipboardManager.current

    Box(
        modifier = modifier
            .background(
                color = districtDropDownMenuColor,
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 12.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = uri,
                fontSize = 14.sp
            )
            Spacer(Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        clipboardManager.setText(AnnotatedString(uri))
                    },
                painter = painterResource(com.example.core.R.drawable.copy),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun UnEditableContactComponentPreview() {
    UnEditableContactComponent(
        uri = "https://vk.com/urchuko",
        icon = com.example.core.R.drawable.ic_vk
    )
}