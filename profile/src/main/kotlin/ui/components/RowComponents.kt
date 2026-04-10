package ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.model.PetInfo

@Composable
fun StatusRow(
    modifier: Modifier = Modifier,
    petInfo: PetInfo
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (petInfo.petCategory == PetInfo.PetCategory.Cat) "Кот" else "Собака",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Box(
            modifier = Modifier
                .background(
                    color = if (petInfo.isFound) Color(0xFF3DD74C) else Color(0xFFD73D3D),
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Text(
                text = if (petInfo.isFound) "Найден" else "Потерян",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}


@Preview
@Composable
private fun StatusRowPreview() {
    StatusRow(
        petInfo = PetInfo(
            image = R.drawable.cat_found,
            petCategory = PetInfo.PetCategory.Cat,
            name = "Кот",
            isFound = false,
            address = "Кировский, Сергея Лазо",
            date = "18.02",
            time = "Утром"
        )
    )
}

@Composable
fun InfoRow(
    modifier: Modifier = Modifier,
    petInfo: String,
    @DrawableRes drawableRes: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(drawableRes),
            contentDescription = null
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = petInfo,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
private fun InfoRowPreview() {
    InfoRow(
        Modifier,
        "18.02",
        drawableRes = R.drawable.ic_point
    )
}