package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.theme.Ser
import ui.theme.backgroundColor
import ui.theme.buttonPrimary
import ui.theme.buttonSecondPrimary

@Composable
fun ProfileLegalInfoScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ToolBar(
            toolBarInfo = ToolBarInfo(
                title = "Правовая информация",
                backArrow = true
            ),
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                LegalHeaderCard()
            }

            item {
                LegalChapterTitle("Политика конфиденциальности")
            }

            items(privacySections) { section ->
                LegalSectionCard(section = section)
            }

            item {
                LegalChapterTitle("Пользовательское соглашение")
            }

            items(termsSections) { section ->
                LegalSectionCard(section = section)
            }

            item {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun LegalHeaderCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(buttonSecondPrimary)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "По следам",
                color = buttonPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Здесь собраны правила обработки данных и публикации объявлений. Они помогают безопасно использовать карту, фото питомцев и уведомления.",
                color = Color(0xFF3E3768),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Дата обновления: 10 июня 2026",
                color = Ser,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun LegalChapterTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(top = 8.dp),
        text = title,
        color = Color.Black,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun LegalSectionCard(
    section: LegalSection,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = section.title,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (section.description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = section.description,
                color = Ser,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        section.bullets.forEach { bullet ->
            Spacer(Modifier.height(8.dp))
            LegalBullet(text = bullet)
        }
    }
}

@Composable
private fun LegalBullet(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(buttonPrimary)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            color = Ser,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

private data class LegalSection(
    val title: String,
    val description: String = "",
    val bullets: List<String> = emptyList()
)

private val privacySections = listOf(
    LegalSection(
        title = "Какие данные используются",
        bullets = listOf(
            "данные профиля: имя, описание, аватар и контакты, которые пользователь указывает самостоятельно;",
            "данные объявлений: фотографии питомца, описание, порода, окрас, пол, дата, время и выбранная точка на карте;",
            "геолокация устройства, если пользователь дал разрешение;",
            "push-токен устройства для отправки уведомлений;",
            "технические данные, нужные для стабильной работы приложения и диагностики ошибок."
        )
    ),
    LegalSection(
        title = "Для чего нужны данные",
        bullets = listOf(
            "создавать и показывать объявления о потерянных и найденных питомцах;",
            "показывать место события на карте и помогать выбрать текущую точку;",
            "отправлять уведомления, связанные с объявлениями и работой приложения;",
            "обрабатывать обращения пользователей и улучшать качество сервиса;",
            "защищать приложение от злоупотреблений, спама и ложных публикаций."
        )
    ),
    LegalSection(
        title = "Геолокация",
        description = "Приложение может использовать геолокацию для установки камеры карты на текущую позицию, выбора места объявления и показа объявлений рядом. Если пользователь отдельно разрешил фоновую геолокацию, она используется только для функций поиска и уведомлений, связанных с питомцами рядом."
    ),
    LegalSection(
        title = "Фото и объявления",
        description = "Фотографии, описания и выбранные точки на карте могут быть видны другим пользователям приложения. Пользователь отвечает за то, что имеет право публиковать добавленные материалы."
    ),
    LegalSection(
        title = "Передача данных",
        description = "Мы не продаем персональные данные. Данные могут передаваться только сервисам, которые нужны для работы приложения, или в случаях, предусмотренных законом.",
        bullets = listOf(
            "серверу и базе данных приложения;",
            "Firebase и сервисам Google для уведомлений, авторизации или диагностики;",
            "Yandex MapKit и картографическим сервисам для работы карты;",
        )
    ),
    LegalSection(
        title = "Хранение и удаление",
        description = "Данные хранятся столько, сколько нужно для работы приложения и опубликованных объявлений. Пользователь может запросить удаление аккаунта и связанных данных через поддержку или доступную функцию удаления в приложении."
    ),
    LegalSection(
        title = "Права пользователя",
        bullets = listOf(
            "запросить информацию о своих данных;",
            "исправить неточные данные профиля;",
            "удалить объявления, если такая функция доступна;",
            "запросить удаление аккаунта и персональных данных;",
            "отозвать разрешения на геолокацию, камеру, фото и уведомления в настройках устройства."
        )
    )
)

private val termsSections = listOf(
    LegalSection(
        title = "Правила публикации",
        bullets = listOf(
            "публикуйте только достоверные объявления о потерянных или найденных питомцах;",
            "не добавляйте чужие фотографии и персональные данные без разрешения;",
            "не размещайте оскорбления, угрозы, спам, мошеннические объявления и незаконный контент;",
            "не используйте приложение для незаконной продажи животных."
        )
    ),
    LegalSection(
        title = "Модерация",
        description = "Разработчик может скрывать или удалять объявления, ограничивать доступ к приложению и рассматривать жалобы, если контент нарушает правила, закон или требования Google Play."
    ),
    LegalSection(
        title = "Ответственность",
        description = "Приложение является информационной площадкой и не гарантирует нахождение питомца, точность пользовательских объявлений или действия других пользователей."
    ),
    LegalSection(
        title = "Контакты",
        description = "По вопросам конфиденциальности, правил приложения или удаления данных можно обратиться на email поддержки, указанный на странице приложения в Google Play."
    )
)
