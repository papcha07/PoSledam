package ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.profile.UnEditableContactComponent

@Composable
fun ContactListComponent(
    modifier: Modifier = Modifier,
    vkUri: String? = null,
    tgUri: String? = null
) {
    when {
        vkUri != null && tgUri != null -> {
            Column(
                modifier = modifier.fillMaxWidth()
            ) {
                UnEditableContactComponent(
                    uri = vkUri,
                    icon = com.example.core.R.drawable.ic_vk
                )
                Spacer(Modifier.height(8.dp))
                UnEditableContactComponent(
                    uri = tgUri,
                    icon = com.example.core.R.drawable.ic_tg
                )
            }
        }

        tgUri != null -> {
            UnEditableContactComponent(
                uri = tgUri,
                icon = com.example.core.R.drawable.ic_tg
            )
        }

        vkUri != null -> {
            UnEditableContactComponent(
                uri = vkUri,
                icon = com.example.core.R.drawable.ic_vk
            )
        }
    }
}