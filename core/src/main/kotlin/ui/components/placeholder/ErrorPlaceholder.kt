package ui.components.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import ui.theme.Ser

@Composable
fun ErrorPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(R.drawable.dogs),
            contentDescription = "Пустой список",
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.padding(top = 8.dp))
        Text(
            text = "Проблемы с соединением",
            fontSize = 18.sp
        )
        Spacer(Modifier.padding(top = 10.dp))
        Text(
            text = "Проверьте подключение",
            fontSize = 14.sp,
            color = Ser
        )
    }
}

@Preview
@Composable
private fun ErrorPlaceholderPreview() {
    ErrorPlaceholder()
}