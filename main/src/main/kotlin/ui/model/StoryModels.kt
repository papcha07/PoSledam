package ui.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.core.R

enum class StoryId {
    LostPetFirstSteps,
    PetEscapes,
    NicheDogBreeds
}

enum class StorySlideLayout {
    Title,
    Body,
    Centered
}

data class StoryInfo(
    val id: StoryId,
    val previewTitle: String,
    @DrawableRes val previewImage: Int,
    val slides: List<StorySlide>
)

data class StorySlide(
    @DrawableRes val backgroundImage: Int? = null,
    val backgroundColor: Color = Color.Black,
    val title: String,
    val subtitle: String? = null,
    val body: String? = null,
    val footerTitle: String? = null,
    val footerBody: String? = null,
    val layout: StorySlideLayout = StorySlideLayout.Body,
    val textColor: Color = Color.White,
    val scrimStyle: StoryScrimStyle = StoryScrimStyle.Dark
)

enum class StoryScrimStyle {
    Dark,
    None
}

val storyInfoList = listOf(
    StoryInfo(
        id = StoryId.LostPetFirstSteps,
        previewTitle = "Первые действия при пропаже питомца",
        previewImage = R.drawable.story_lost_pet_intro,
        slides = listOf(
            StorySlide(
                backgroundImage = R.drawable.story_lost_pet_intro,
                title = "Первые действия, которые могут сильно помочь",
                subtitle = "Не паникуйте. Первые 30 минут могут сыграть решающую роль",
                layout = StorySlideLayout.Title
            ),
            StorySlide(
                backgroundImage = R.drawable.story_lost_pet_last_place,
                title = "Вернитесь в последнее место, где вы видели питомца",
                body = "Ищите рядом: подъезды, подвалы, кусты, парковки, стройки"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_lost_pet_search_area,
                title = "Осмотрите всё вокруг",
                body = "Загляните:\n• под машины;\n• в кусты;\n• в подъезды, подвалы;\n• на стройки и парковки",
                footerBody = "Испуганные питомцы часто выбирают укрытия"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_lost_pet_call_calmly,
                title = "Зовите спокойно",
                body = "Используйте привычную кличку и спокойный голос.\n\nВозьмите любимое лакомство или игрушку, знакомые запахи помогут питомцу выйти"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_lost_pet_announcement,
                title = "Не откладывайте объявление",
                body = "Добавьте:\n• чёткую фотографию;\n• место пропажи;\n• время;\n• особые приметы",
                footerBody = "Чем раньше люди увидят информацию, тем выше шанс найти питомца"
            )
        )
    ),
    StoryInfo(
        id = StoryId.PetEscapes,
        previewTitle = "Почему питомцы убегают",
        previewImage = R.drawable.story_escape_preview,
        slides = listOf(
            StorySlide(
                backgroundImage = R.drawable.story_escape_intro,
                title = "Почему питомцы убегают",
                subtitle = "И когда это особенно опасно",
                layout = StorySlideLayout.Title
            ),
            StorySlide(
                backgroundImage = R.drawable.story_escape_storm,
                title = "Гроза и петарды",
                subtitle = "Резкий звук — и инстинкт один: бежать",
                body = "Питомец не контролирует это. Даже спокойная собака может сорваться с поводка от одного хлопка",
                footerTitle = "Что сделать?",
                footerBody = "Не выгуливать без поводка в праздники и в грозу"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_escape_unknown_place,
                title = "Незнакомое место",
                body = "Дача, гости, новый район на прогулке. Питомец не знает куда возвращаться — нет запомненного маршрута домой\n\nИменно поэтому большинство побегов случается в первые дни на новом месте",
                footerTitle = "Что сделать?",
                footerBody = "Не отпускать без поводка пока не прошло минимум 2 недели на новом месте"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_escape_smell,
                title = "Течка и запах",
                body = "Кот или некастрированный пёс чует запах на расстоянии нескольких кварталов. Это сильнее любой дрессировки\n\nУходят тихо, без предупреждения, и могут пройти несколько километров",
                footerTitle = "Что сделать?",
                footerBody = "Стерилизация или строгий контроль выгула в период течки"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_escape_open_door,
                title = "Открытая дверь / ворота",
                body = "Самая частая причина и самая обидная. Курьер, гость, ребёнок — секунда невнимания\n\nПитомец не убегал специально, просто увидел открытое и пошёл исследовать",
                footerTitle = "Что сделать?",
                footerBody = "Адресник на ошейнике. Если питомца найдут через 10 минут — адресник решит всё без объявлений и чатов"
            ),
            StorySlide(
                backgroundColor = Color(0xFFF8DEDE),
                title = "Ни одна из этих ситуаций не предупреждает заранее",
                body = "Адресник, актуальное фото в телефоне, чип — это не паранойя",
                footerTitle = "Если питомец всё-таки пропал — создай объявление прямо сейчас",
                textColor = Color(0xFF7A2B2A),
                scrimStyle = StoryScrimStyle.None
            )
        )
    ),
    StoryInfo(
        id = StoryId.NicheDogBreeds,
        previewTitle = "Самые нишевые породы собак",
        previewImage = R.drawable.story_breeds_preview,
        slides = listOf(
            StorySlide(
                backgroundImage = R.drawable.story_breeds_cover,
                title = "Самые нишевые породы собак",
                subtitle = "Проверь, сколько из них ты знаешь",
                layout = StorySlideLayout.Title
            ),
            StorySlide(
                backgroundImage = R.drawable.story_breeds_sloughi,
                title = "Слюги",
                body = "Арабская борзая из Северной Африки. Разгоняется до 60 км/ч, но дома — тихий интроверт: диван любит больше охоты"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_breeds_borzoi,
                title = "Русская псовая борзая",
                body = "Аристократ из царской России. Профиль — как у поэта Серебряного века, а на прогулке внезапно включает пятую передачу"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_breeds_lundehund,
                title = "Норвежский лундехунд",
                body = "Создан для охоты на тупиков в скалах. Шесть пальцев на каждой лапе, а уши складываются в трубочку — серьёзно"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_breeds_mudi,
                title = "Муди",
                body = "Венгерский пастух с кудрявой шерстью. Пасёт овец, ищет грибы и выигрывает в аджилити — всё в один день"
            ),
            StorySlide(
                backgroundImage = R.drawable.story_breeds_final,
                title = "Правда нишево?",
                layout = StorySlideLayout.Centered
            )
        )
    )
)

fun findStoryInfo(storyId: StoryId): StoryInfo? {
    return storyInfoList.firstOrNull { it.id == storyId }
}
