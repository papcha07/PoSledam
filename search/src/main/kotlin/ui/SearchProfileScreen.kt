package ui

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.core.R
import ui.BASE_URL
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.components.placeholder.ShimmerImagePlaceholder

data class SearchProfileUi(
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val contacts: List<SearchContactUi>
)

data class SearchContactUi(
    val iconRes: Int,
    val label: String
)

@Composable
fun SearchProfileScreen(
    modifier: Modifier = Modifier,
    profile: SearchProfileUi,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F7))
    ) {
        ToolBar(
            modifier = Modifier,
            toolBarInfo = ToolBarInfo(
                title = "Профиль",
                backArrow = true
            ),
            onBackClick = onBackClick
        )

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            ProfileHeaderCard(profile = profile)
            Spacer(Modifier.height(12.dp))
            ContactsCard(contacts = profile.contacts)
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    profile: SearchProfileUi
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profile.avatarUrl?.let { "$BASE_URL/api/image/$it" })
                    .crossfade(true)
                    .build(),
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    ShimmerImagePlaceholder(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                    )
                },
                error = {
                    Image(
                        painter = painterResource(R.drawable.purple_dog_article),
                        contentDescription = null,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = profile.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = profile.description,
                    fontSize = 14.sp,
                    color = Color(0xFF8E8E93)
                )
            }
        }
    }
}

@Composable
private fun ContactsCard(
    contacts: List<SearchContactUi>
) {
    // Если контакты пустые – показываем "пустые" поля,
    // чтобы верстка не схлопывалась.
    val effectiveContacts = if (contacts.isEmpty()) {
        listOf(
            SearchContactUi(iconRes = R.drawable.ic_tg, label = ""),
            SearchContactUi(iconRes = R.drawable.ic_vk, label = "")
        )
    } else {
        contacts
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Контакты",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            effectiveContacts.forEach { contact ->
                ContactRow(contact = contact)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: SearchContactUi
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F7),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(contact.iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = contact.label,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(R.drawable.copy),
            contentDescription = "Скопировать",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun SearchProfileScreenPreview() {
    val profile = SearchProfileUi(
        name = "Алена Янченко",
        description = "Пытаюсь помочь потеряшкам вернуться домой",
        avatarUrl = null,
        contacts = listOf(
            SearchContactUi(
                iconRes = R.drawable.ic_tg,
                label = "t.me/milsviii"
            ),
            SearchContactUi(
                iconRes = R.drawable.ic_vk,
                label = "vk.ru/milsviii"
            )
        )
    )
    SearchProfileScreen(profile = profile, onBackClick = {})
}

