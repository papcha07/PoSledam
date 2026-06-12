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
    tgUri: String? = null,
    whUri: String? = null
) {
    val contacts = listOfNotNull(
        vkUri?.let { ContactItem(uri = it, icon = com.example.core.R.drawable.ic_vk) },
        whUri?.let { ContactItem(uri = it, icon = com.example.core.R.drawable.ic_whatsapp) },
        tgUri?.let { ContactItem(uri = it, icon = com.example.core.R.drawable.ic_tg) }
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        contacts.forEachIndexed { index, contact ->
            UnEditableContactComponent(
                uri = contact.uri,
                icon = contact.icon
            )
            if (index < contacts.lastIndex) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private data class ContactItem(
    val uri: String,
    val icon: Int
)
