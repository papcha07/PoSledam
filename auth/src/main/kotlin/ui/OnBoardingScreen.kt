package ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.core.R
import ui.model.OnboardingData
import ui.theme.ActivePagerState
import ui.theme.EnterButton
import ui.theme.Primary
import ui.theme.RegisterButton
import ui.theme.UnActivePagerState

@Composable
fun OnBoardingScreen(navigate: () -> Unit) {
    val onboardingData = listOf(
        OnboardingData(
            title = "Потеряли своего питомца?",
            description = "Разместите информацию о пропаже вашего питомца и дайте шанс на возвращение домой",
            image = R.drawable.ic_cat
        ),
        OnboardingData(
            title = "Нашли питомца на улице??",
            description = "Присоединяйтесь к сообществу, где вместе мы можем вернуть каждого потерянного друга домой",
            image = R.drawable.ic_dog
        ),
        OnboardingData(
            title = "Найдите своего друга",
            description = "Мы подберем и предложим вам друга из проверенных людей",
            image = R.drawable.ic_catwoman
        ),
    )

    val pagerState = rememberPagerState(pageCount = { onboardingData.size })

    ConstraintLayout(Modifier
        .fillMaxSize()
        .background(Color.White)) {
        val (bottomContainer, image) = createRefs()

        ImageContainer(Modifier.constrainAs(image){
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(bottomContainer.top)
        }, onboardingData[pagerState.currentPage].image)

        PagerContainer(
            Modifier.constrainAs(
                bottomContainer
            ) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }, pagerState, onboardingData,
            navigateToEnter = {
                navigate()
            }
        )
    }
}

@Preview
@Composable
fun OnBoardingScreenPreview() {
    OnBoardingScreen(
        navigate = {  }
    )
}

@Composable
fun ButtonSection(
    modifier: Modifier,
    onNextClick: () -> Unit
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
        ,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {

            },
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RegisterButton
            )
        ) {
            Text(text = "Skip", color = Color.Black)
        }

        Button(
            onClick = {
                onNextClick()
            },
            modifier = Modifier
                .weight(2f)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EnterButton
            )
        ) {
            Text(text = "Продолжить")
        }
    }
}

@Composable
fun ImageContainer(modifier: Modifier, @DrawableRes imageRes: Int) {
    Box(
        modifier
            .fillMaxWidth()
            .background(color = Primary)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "dogs",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.padding(top = 96.dp),
        )
    }
}

@Composable
fun PagerContainer(
    modifier: Modifier,
    pagerState: PagerState,
    onboardingData: List<OnboardingData>,
    navigateToEnter: () -> Unit
) {
    Column(modifier
        .fillMaxWidth()
        .background(Color.White)
    ) {
        HorizontalPager(
            state = pagerState,
        ) { page ->
            OnboardingContentCard(
                title = onboardingData[page].title,
                description = onboardingData[page].description,
            )
        }
        PageIndicator(modifier, pagerState.pageCount, pagerState.currentPage)
        ButtonSection(
            modifier,
            onNextClick = { navigateToEnter() },
        )
    }
}

@Composable
fun PageIndicator(modifier: Modifier,pageCount: Int, currentPage: Int) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 44.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .width(if (index == currentPage) 26.dp else 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        color = if (index == currentPage) ActivePagerState else UnActivePagerState
                    )
            )
            if (index < pageCount - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }

}

@Composable
fun OnboardingContentCard(
    title: String,
    description: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(328.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                fontSize = 24.sp,
            )
            Spacer(Modifier.height(20.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = description,
                fontSize = 20.sp,
                color = Color(0xFF1E1E1E)
            )
        }
    }
}