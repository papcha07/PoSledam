package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import ui.BASE_URL
import ui.model.PetUiPreview
import ui.theme.addressSearchColor

@Composable
fun AnimalCard(
    modifier: Modifier = Modifier,
    petInfo: PetUiPreview,
    currentState: Int,
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(15.dp),
                clip = false,
                spotColor = Color.Black.copy(alpha = 0.35f),
                ambientColor = Color.Black.copy(alpha = 0.35f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .clip(RoundedCornerShape(15.dp))
            .clickable {
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Box {
                val imageUrl = "$BASE_URL/api/image/${petInfo.imageUrl}"
                println("Loading image from: $imageUrl")
                AsyncImage(
                    modifier = Modifier
                        .size(130.dp, 140.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    model = "$BASE_URL/api/image/${petInfo.imageUrl}",
                    placeholder = painterResource(R.drawable.ic_dog),
                    error = painterResource(R.drawable.ic_dog),
                    contentDescription = null,
                    onError = {
                        println("Image loading failed: ${it.result.throwable?.message}")
                        it.result.throwable?.printStackTrace()
                    },
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = if (currentState == 0) Color.Red else Color.Green)
                        .padding(top = 20.dp, start = 20.dp)
                )
            }
            Spacer(Modifier.width(14.dp))

            Box(
                modifier = Modifier.height(140.dp)
            ) {
                Column(
                    Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = petInfo.breed,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (petInfo.description.length > 110) {
                            "${
                                petInfo.description.dropLast(30)
                            }..."
                        } else {
                            petInfo.description
                        },

                        fontSize = 12.sp
                    )
                }
                Text(
                    modifier = Modifier.align(Alignment.BottomStart),
                    text = petInfo.district?.uppercase() ?: "Нет района",
                    fontSize = 14.sp,
                    color = addressSearchColor
                )
            }

        }
    }
}
