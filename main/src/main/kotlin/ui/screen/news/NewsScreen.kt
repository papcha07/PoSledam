import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R

enum class NewsType {
    RobberyNews,
    SelfWaklingNews,
    HowToUseAppNews
}

data class NewsData(
    @StringRes val firstHeader: Int,
    @StringRes val firstText: Int,
    @StringRes val secondHeader: Int,
    @StringRes val secondText: Int,
    @StringRes val thirdHeader: Int,
    @StringRes val thirdText: Int,
    @DrawableRes val image: Int
)

private val newsMap = mapOf(
    NewsType.RobberyNews to NewsData(
        firstHeader = R.string.news_title,
        firstText = R.string.news_description,
        secondHeader = R.string.news_step1_title,
        secondText = R.string.news_step1_description,
        thirdHeader = R.string.news_step2_title,
        thirdText = R.string.news_step2_description,
        image = R.drawable.news
    ),

    NewsType.SelfWaklingNews to NewsData(
        firstHeader = R.string.self_walking_title,
        firstText = R.string.self_walking_description,
        secondHeader = R.string.self_walking_step1_title,
        secondText = R.string.self_walking_step1_description,
        thirdHeader = R.string.self_walking_step2_title,
        thirdText = R.string.self_walking_step2_description,
        image = R.drawable.news2
    ),
    NewsType.HowToUseAppNews to NewsData(
        firstHeader = R.string.app_guide_title,
        firstText = R.string.app_guide_description,
        secondHeader = R.string.app_guide_step1_title,
        secondText = R.string.app_guide_step1_description,
        thirdHeader = R.string.app_guide_step2_title,
        thirdText = R.string.app_guide_step2_description,
        image = R.drawable.news3
    )
)


@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    newsType: NewsType,
    goBackClick: () -> Unit
) {
    val currentNews = newsMap[newsType] ?: return

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
            text = stringResource(currentNews.firstHeader),
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 20.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(currentNews.firstText),
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 14.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(currentNews.secondHeader),
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 18.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(currentNews.secondText),
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 14.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Image(
            painter = painterResource(id = currentNews.image),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(currentNews.thirdHeader),
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 18.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(currentNews.thirdText),
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 14.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}