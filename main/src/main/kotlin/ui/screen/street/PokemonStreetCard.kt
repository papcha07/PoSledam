package ui.screen.street

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import com.yandex.mapkit.geometry.Point
import domain.models.AdvertInfo
import ui.components.CurrentLocationMap
import ui.components.EventDateComponent
import ui.components.other.TextFieldComponent
import ui.model.data.TextFieldData
import ui.theme.buttonPrimary

@Composable
fun PokemonStreetCard(
    modifier: Modifier = Modifier,
    photos: List<Uri>,
    advertState: AdvertInfo,
    cardStats: PokemonCardStats,
    addDescription: (String) -> Unit
) {
    val theme = cardStats.theme

    Box(
        modifier = modifier
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(22.dp),
                clip = false
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(theme.frameStart, theme.frameEnd)
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(theme.field, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            PokemonCardHeader(cardStats = cardStats)
            Spacer(Modifier.height(12.dp))
            PokemonPhotoBezel(photos = photos)
            Spacer(Modifier.height(10.dp))
            SilverRibbon(text = "№ 042 · ${advertState.locationLabel()}")
            Spacer(Modifier.height(16.dp))

            PokemonAttackSection(
                theme = theme,
                energyCount = 1,
                title = "Где нашли"
            ) {
                CurrentLocationMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    currentLocation = Point(
                        advertState.lat,
                        advertState.lon
                    ),
                    onLocationResolved = { _, _ -> }
                )
            }

            PokemonAttackSection(
                theme = theme,
                energyCount = 2,
                title = "Когда нашли"
            ) {
                EventDateComponent(
                    advertState = advertState.eventDate,
                    announcementType = 0
                )
            }

            PokemonAttackSection(
                theme = theme,
                energyCount = 3,
                title = "Описание места",
                showDivider = false
            ) {
                TextFieldComponent(
                    value = advertState.placeDescription,
                    textFieldData = TextFieldData(
                        label = "Описание",
                        hint = "Введите описание места"
                    ),
                    onValueChange = addDescription
                )
            }

            Spacer(Modifier.height(18.dp))
            PokemonCardFooter(cardStats = cardStats)
        }
    }
}

@Composable
private fun PokemonCardHeader(
    cardStats: PokemonCardStats
) {
    val theme = cardStats.theme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "БАЗОВЫЙ",
                color = Color(0xFF2B2B2B),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.weight(1f),
            text = "Уличный зверь",
            color = theme.ink,
            fontFamily = LebowskiByPragmatica,
            fontSize = 22.sp,
            lineHeight = 24.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.width(8.dp))
        PokemonTypeBadge(type = cardStats.type)
    }
}

@Composable
private fun PokemonTypeBadge(
    type: PokemonPetType
) {
    Row(
        modifier = Modifier
            .widthIn(max = 124.dp)
            .clip(CircleShape)
            .background(type.color)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        PokemonCardIconCanvas(
            icon = type.icon,
            tint = Color.White,
            background = type.color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = type.label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PokemonPhotoBezel(
    photos: List<Uri>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(252.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFEDEDF1), Color(0xFFB9BAC1))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(6.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp)),
            model = photos.firstOrNull(),
            placeholder = painterResource(R.drawable.ic_dog),
            error = painterResource(R.drawable.ic_dog),
            contentScale = ContentScale.Crop,
            contentDescription = "Фото найденного животного"
        )

        if (photos.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.42f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${photos.size} фото",
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SilverRibbon(
    text: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFEDEDF1), Color(0xFFB9BAC1))
                )
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF2B2B2B),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PokemonAttackSection(
    theme: PokemonCardTheme,
    energyCount: Int,
    title: String,
    showDivider: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            EnergyIcons(
                count = energyCount,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 2.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 76.dp),
                    text = title,
                    color = theme.ink,
                    fontFamily = LebowskiByPragmatica,
                    fontSize = 19.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(9.dp))
                content()
            }
        }

        if (showDivider) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(theme.ink.copy(alpha = 0.15f))
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EnergyIcons(
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.width(70.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(count) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(buttonPrimary),
                contentAlignment = Alignment.Center
            ) {
                PokemonCardIconCanvas(
                    icon = PokemonCardIcon.Paw,
                    tint = Color.White,
                    background = buttonPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun PokemonCardFooter(
    cardStats: PokemonCardStats
) {
    val theme = cardStats.theme
    Column(modifier = Modifier.fillMaxWidth()) {
        FooterTitle(text = "СТАТЫ", theme = theme)
        Spacer(Modifier.height(10.dp))
        cardStats.stats.forEach { stat ->
            StatProgressRow(theme = theme, stat = stat)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(8.dp))
        FooterTitle(text = "СЛАБОСТИ", theme = theme)
        Spacer(Modifier.height(10.dp))
        cardStats.weaknesses.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { weakness ->
                    WeaknessChip(
                        modifier = Modifier.weight(1f),
                        weakness = weakness
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(4.dp))
        MetricsRow(
            theme = theme,
            weight = cardStats.weight,
            height = cardStats.height
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "©2026 ПО СЛЕДАМ   Илл. Вы   ${cardStats.cardNumber}/151",
            color = theme.ink.copy(alpha = 0.6f),
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FooterTitle(
    text: String,
    theme: PokemonCardTheme
) {
    Text(
        text = text,
        color = theme.accent,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1
    )
}

@Composable
private fun StatProgressRow(
    theme: PokemonCardTheme,
    stat: PokemonStatRow
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(118.dp),
            text = stat.label,
            color = theme.ink,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape)
                .background(theme.ink.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(stat.value / 100f)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                theme.accent.copy(alpha = 0.75f),
                                theme.accent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun WeaknessChip(
    modifier: Modifier = Modifier,
    weakness: PokemonWeakness
) {
    Row(
        modifier = modifier
            .heightIn(min = 30.dp)
            .clip(CircleShape)
            .background(weakness.color)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PokemonCardIconCanvas(
            icon = weakness.icon,
            tint = Color.White,
            background = weakness.color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            modifier = Modifier.weight(1f),
            text = weakness.label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MetricsRow(
    theme: PokemonCardTheme,
    weight: String,
    height: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricItem(
            modifier = Modifier.weight(1f),
            theme = theme,
            label = weight,
            icon = MetricIcon.Weight
        )
        MetricItem(
            modifier = Modifier.weight(1f),
            theme = theme,
            label = height,
            icon = MetricIcon.Height
        )
    }
}

@Composable
private fun MetricItem(
    modifier: Modifier = Modifier,
    theme: PokemonCardTheme,
    label: String,
    icon: MetricIcon
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(theme.ink.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricIconCanvas(
            icon = icon,
            tint = theme.ink,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            color = theme.ink,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PokemonCardIconCanvas(
    icon: PokemonCardIcon,
    tint: Color,
    background: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.12f

        when (icon) {
            PokemonCardIcon.Paw -> {
                drawCircle(tint, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.66f))
                drawCircle(tint, radius = w * 0.13f, center = Offset(w * 0.25f, h * 0.38f))
                drawCircle(tint, radius = w * 0.13f, center = Offset(w * 0.42f, h * 0.25f))
                drawCircle(tint, radius = w * 0.13f, center = Offset(w * 0.6f, h * 0.25f))
                drawCircle(tint, radius = w * 0.13f, center = Offset(w * 0.76f, h * 0.38f))
            }

            PokemonCardIcon.Home -> {
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.12f, h * 0.48f)
                        lineTo(w * 0.5f, h * 0.12f)
                        lineTo(w * 0.88f, h * 0.48f)
                        close()
                    },
                    color = tint
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.22f, h * 0.45f),
                    size = Size(w * 0.56f, h * 0.42f),
                    cornerRadius = CornerRadius(w * 0.08f)
                )
            }

            PokemonCardIcon.Moon -> {
                drawCircle(tint, radius = w * 0.42f, center = Offset(w * 0.48f, h * 0.48f))
                drawCircle(background, radius = w * 0.36f, center = Offset(w * 0.62f, h * 0.38f))
            }

            PokemonCardIcon.Bolt -> {
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.56f, h * 0.05f)
                        lineTo(w * 0.22f, h * 0.55f)
                        lineTo(w * 0.48f, h * 0.55f)
                        lineTo(w * 0.36f, h * 0.95f)
                        lineTo(w * 0.82f, h * 0.38f)
                        lineTo(w * 0.54f, h * 0.38f)
                        close()
                    },
                    color = tint
                )
            }

            PokemonCardIcon.ForkKnife -> {
                drawLine(tint, Offset(w * 0.24f, h * 0.15f), Offset(w * 0.24f, h * 0.9f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.14f, h * 0.15f), Offset(w * 0.14f, h * 0.38f), stroke * 0.65f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.34f, h * 0.15f), Offset(w * 0.34f, h * 0.38f), stroke * 0.65f, StrokeCap.Round)
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.62f, h * 0.14f),
                    size = Size(w * 0.16f, h * 0.76f),
                    cornerRadius = CornerRadius(w * 0.08f)
                )
            }

            PokemonCardIcon.Palm -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.35f, h * 0.34f),
                    size = Size(w * 0.34f, h * 0.46f),
                    cornerRadius = CornerRadius(w * 0.14f)
                )
                repeat(4) { index ->
                    drawCircle(
                        color = tint,
                        radius = w * 0.09f,
                        center = Offset(w * (0.24f + index * 0.16f), h * 0.28f)
                    )
                }
            }

            PokemonCardIcon.Box -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.15f, h * 0.28f),
                    size = Size(w * 0.7f, h * 0.55f),
                    cornerRadius = CornerRadius(w * 0.08f)
                )
                drawLine(background, Offset(w * 0.18f, h * 0.45f), Offset(w * 0.82f, h * 0.45f), stroke * 0.6f)
            }

            PokemonCardIcon.Target -> {
                drawCircle(tint, radius = w * 0.4f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(stroke))
                drawCircle(tint, radius = w * 0.22f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(stroke))
                drawCircle(tint, radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.5f))
            }

            PokemonCardIcon.Speech -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.12f, h * 0.18f),
                    size = Size(w * 0.76f, h * 0.54f),
                    cornerRadius = CornerRadius(w * 0.16f)
                )
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.36f, h * 0.7f)
                        lineTo(w * 0.28f, h * 0.92f)
                        lineTo(w * 0.54f, h * 0.7f)
                        close()
                    },
                    color = tint
                )
            }
        }
    }
}

@Composable
private fun MetricIconCanvas(
    icon: MetricIcon,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.1f
        when (icon) {
            MetricIcon.Weight -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.22f, h * 0.34f),
                    size = Size(w * 0.56f, h * 0.5f),
                    cornerRadius = CornerRadius(w * 0.12f)
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.16f,
                    center = Offset(w * 0.5f, h * 0.28f),
                    style = Stroke(stroke)
                )
            }

            MetricIcon.Height -> {
                drawLine(tint, Offset(w * 0.5f, h * 0.12f), Offset(w * 0.5f, h * 0.88f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.3f, h * 0.27f), Offset(w * 0.5f, h * 0.1f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.7f, h * 0.27f), Offset(w * 0.5f, h * 0.1f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.3f, h * 0.73f), Offset(w * 0.5f, h * 0.9f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.7f, h * 0.73f), Offset(w * 0.5f, h * 0.9f), stroke, StrokeCap.Round)
            }
        }
    }
}

private fun AdvertInfo.locationLabel(): String {
    return when {
        address.isNotBlank() -> address
        lat == 0.0 && lon == 0.0 -> "Определяем место..."
        else -> "Место обнаружения выбрано"
    }
}

private enum class MetricIcon {
    Weight,
    Height
}

private val LebowskiByPragmatica = FontFamily(
    Font(R.font.lebowski_by_pragmatica_regular)
)
