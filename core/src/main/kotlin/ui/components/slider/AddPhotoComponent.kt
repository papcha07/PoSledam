package ui.components.slider

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.theme.Ser

@Composable
fun AddPhotoComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .testTag("image_button")
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .background(color = Color(0xFFE9E9E9), shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(232.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .testTag("placeholder_image"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.padding(10.dp),
                painter = painterResource(R.drawable.ic_uploadpet),
                contentDescription = "Добавить изображение"
            )
            Spacer(Modifier.height(8.dp))
            Text("Добавить фото питомца", fontSize = 12.sp, color = Ser)
        }
    }
}

@Preview
@Composable
private fun AddPhotoComponent() {
    AddPhotoComponent{
    }
}