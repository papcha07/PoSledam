import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    goBackClick: () -> Unit
) {
    BackHandler {
        goBackClick()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),

        ) {
        Text(
            text = "Как помочь питомцу найтись",
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 20.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "По следам предоставляет уникальную платформу для " +
                    "поиска и воссоединения потерянных питомцев с их " +
                    "владельцами",
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 14.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Разместите объявление",
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 18.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Первый и самый важный шаг – разместить " +
                    "объявление о пропаже вашего питомца в нашем " +
                    "приложении. Заполните все необходимые данные - " +
                    "фотографии вашего питомца, его описание, место и " +
                    "время пропажи",
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 14.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Image(
            painter = painterResource(id = R.drawable.news),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Поделитесь информацией",
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 18.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "После размещения объявления поделитесь " +
                    "информацией о потерянном питомце с другими " +
                    "пользователями. Используйте функции социальных " +
                    "сетей, чтобы распространить информацию " +
                    "максимально широко. Чем больше людей узнают о " +
                    "вашей потере, тем больше шансов, что кто-то заметит " +
                    "вашего питомца и свяжется с вами",
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 14.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}