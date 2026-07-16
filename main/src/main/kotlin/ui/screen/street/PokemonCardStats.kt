package ui.screen.street

import androidx.compose.ui.graphics.Color
import java.util.Locale
import kotlin.random.Random

data class PokemonCardStats(
    val theme: PokemonCardTheme,
    val type: PokemonPetType,
    val stats: List<PokemonStatRow>,
    val weaknesses: List<PokemonWeakness>,
    val weight: String,
    val height: String,
    val cardNumber: Int
) {
    companion object {
        fun roll(random: Random = Random.Default): PokemonCardStats {
            return PokemonCardStats(
                theme = cardThemes.random(random),
                type = petTypes.random(random),
                stats = statLabels.map { label ->
                    PokemonStatRow(label = label, value = random.nextInt(25, 101))
                },
                weaknesses = weaknessPool.shuffled(random).take(random.nextInt(2, 4)),
                weight = "${random.nextDouble(1.5, 15.0).toRuDecimal()} кг",
                height = "${random.nextDouble(0.2, 0.71).toRuDecimal()} м",
                cardNumber = random.nextInt(1, 152)
            )
        }
    }
}

data class PokemonCardTheme(
    val frameStart: Color,
    val frameEnd: Color,
    val field: Color,
    val ink: Color,
    val accent: Color
)

data class PokemonPetType(
    val label: String,
    val color: Color,
    val icon: PokemonCardIcon
)

data class PokemonStatRow(
    val label: String,
    val value: Int
)

data class PokemonWeakness(
    val label: String,
    val color: Color,
    val icon: PokemonCardIcon
)

enum class PokemonCardIcon {
    Paw,
    Home,
    Moon,
    Bolt,
    ForkKnife,
    Palm,
    Box,
    Target,
    Speech
}

private val cardThemes = listOf(
    PokemonCardTheme(
        frameStart = Color(0xFFF2CE45),
        frameEnd = Color(0xFFD9A92F),
        field = Color(0xFFFFFBEF),
        ink = Color(0xFF2B2B2B),
        accent = Color(0xFFC99B22)
    ),
    PokemonCardTheme(
        frameStart = Color(0xFFCFE8CB),
        frameEnd = Color(0xFFA8CDA2),
        field = Color(0xFFEFF7ED),
        ink = Color(0xFF23301F),
        accent = Color(0xFF5B8F57)
    ),
    PokemonCardTheme(
        frameStart = Color(0xFF3A2E6E),
        frameEnd = Color(0xFF201741),
        field = Color(0xFF2C2350),
        ink = Color.White,
        accent = Color(0xFFE86BC6)
    ),
    PokemonCardTheme(
        frameStart = Color(0xFF6A6130),
        frameEnd = Color(0xFF423E1E),
        field = Color(0xFF565023),
        ink = Color.White,
        accent = Color(0xFFE8D24B)
    )
)

private val petTypes = listOf(
    PokemonPetType("Бродяга", Color(0xFF6B7A8F), PokemonCardIcon.Paw),
    PokemonPetType("Сосед", Color(0xFF5FA463), PokemonCardIcon.Home),
    PokemonPetType("67", Color(0xFF8E6BC0), PokemonCardIcon.Moon),
    PokemonPetType("Сигма", Color(0xFFC08A3E), PokemonCardIcon.Bolt)
)

private val statLabels = listOf(
    "Милота",
    "Пушистость",
    "Вайб",
    "Дзен",
    "Голод",
    "Скорость убегания"
)

private val weaknessPool = listOf(
    PokemonWeakness("ВКУСНЯШКИ", Color(0xFFE08A3C), PokemonCardIcon.ForkKnife),
    PokemonWeakness("ПОЧЕСУШКИ", Color(0xFFD9679E), PokemonCardIcon.Palm),
    PokemonWeakness("КОРОБКИ", Color(0xFF9A7B4F), PokemonCardIcon.Box),
    PokemonWeakness("ЛАЗЕРНАЯ ТОЧКА", Color(0xFFC0584F), PokemonCardIcon.Target),
    PokemonWeakness("КИС-КИС", Color(0xFF5C9E8F), PokemonCardIcon.Speech)
)

private fun Double.toRuDecimal(): String {
    return String.format(Locale.forLanguageTag("ru-RU"), "%.1f", this)
}
