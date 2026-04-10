package ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import ui.BASE_URL
import ui.theme.Ser

@Composable
fun UserPreviewImage(
    modifier: Modifier = Modifier,
    image: String,
    description: String,
    name: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = "$BASE_URL/api/image/${image}",
            placeholder = painterResource(R.drawable.ic_lapa),
            error = painterResource(R.drawable.ic_dog),
            contentDescription = null,
            onError = {
                println("Image loading failed: ${it.result.throwable?.message}")
                it.result.throwable?.printStackTrace()
            },
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = name,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Ser
            )
        }
    }
}

@Preview
@Composable
private fun UserPreviewComponentPreview() {
    UserPreviewImage(
        image = "dasdasdasd",
        description = "Люблю животных, готов помогать найти их и приютить на время",
        name = "Томми Кэш"
    )
}