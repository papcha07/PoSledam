package ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.components.default_component.SocialTextFieldComponent
import ui.components.default_component.TextFieldData

@Composable
fun ProfileContactComponent(
    modifier: Modifier = Modifier,
    onTgUpdateValue: (String) -> Unit,
    onVkUpdateValue: (String) -> Unit,
    vkValue: String,
    tgValue: String
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Контакты",
            fontSize = 16.sp
        )
        Spacer(Modifier.height(16.dp))
        SocialTextFieldComponent(
            value = vkValue,
            textFieldData = TextFieldData("", ""),
            onValueChange = onVkUpdateValue,
            icon = R.drawable.ic_vk
        )
        Spacer(Modifier.height(8.dp))
        SocialTextFieldComponent(
            value = tgValue,
            textFieldData = TextFieldData("", ""),
            onValueChange = onTgUpdateValue,
            icon = R.drawable.ic_tg
        )
    }
}

@Preview
@Composable
private fun ProfileContactComponentPreview() {
    ProfileContactComponent(
        onTgUpdateValue = {
            "https://vk.com/urchuko"
        },
        onVkUpdateValue = {
            "https://vk.com/urchuko"
        },
        vkValue = "dasd",
        tgValue = "adsda"
    )
}