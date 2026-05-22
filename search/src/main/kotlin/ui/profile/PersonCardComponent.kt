package ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import ui.theme.Ser

@Composable
fun PersonCardComponent(
    modifier: Modifier = Modifier,
    personDto: PersonDto
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(15.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(64.dp),
                    model = personDto.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_dog),
                    error = painterResource(R.drawable.ic_dog)
                )
                Spacer(Modifier.width(14.dp))
                Column(
                    modifier = Modifier
                ) {
                    Text(
                        text = personDto.name,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(8.dp))
                    personDto.description?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = Ser
                        )
                    }

                }
            }

            Spacer(Modifier.height(32.dp))
            Text(text = "Контакты", fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))
            ContactListComponent(
                vkUri = personDto.vkUri,
                tgUri = personDto.tgUri
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}


@Preview
@Composable
private fun PersonCardComponentPreview() {
    PersonCardComponent(
        personDto = PersonDto(
            name = "Томми Кэш",
            uri = null,
            vkUri = "vk.com",
            tgUri = "tg.com",
            description = "Люблю животных, готов помогать найти их и приютить на время"
        )
    )
}