package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import domain.models.FilterDto
import ui.components.default_component.DefaultButton
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.models.TimeFilter
import ui.theme.deleteButtonColor
import ui.theme.deleteButtonText
import ui.theme.districtDropDownMenuColor
import ui.theme.filterItemColor
import ui.viewModel.FilterViewModel

@Composable
fun FiltersScreen(
    modifier: Modifier = Modifier,
    filtersViewModel: FilterViewModel,
    goToSearchScreen: () -> Unit
) {
    val filterState by filtersViewModel.filters.collectAsState()

    Column(Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)

        ) {
            Column {
                ToolBar(
                    toolBarInfo = ToolBarInfo(
                        title = "Фильтрация",
                        backArrow = false,
                        nextRoute = "",
                        prevRoute = "",
                        backArrowIcon = null,
                    )
                )
                SearchRadiusSlider(
                    radius = filterState.searchRadius,
                    onRadiusChange = filtersViewModel::setRadius,
                )
                Spacer(Modifier.height(20.dp))
                //время
                FilterText(text = stringResource(R.string.time))

                Spacer(Modifier.height(8.dp))
                TimeFilterRow(
                    timeList = listOf(
                        TimeFilter.TODAY,
                        TimeFilter.YESTERDAY,
                        TimeFilter.THREE_DAYS,
                        TimeFilter.WEEK,
                        TimeFilter.MONTH,
                    ),
                    filterState = filterState,
                ) {
                    filtersViewModel.setTime(it)
                }
                Spacer(Modifier.height(20.dp))

                //
                FilterText(text = stringResource(R.string.type))
                Spacer(Modifier.height(8.dp))
                TypeOfPetRow(
                    filterState = filterState,
                ) {
                    filtersViewModel.setType(it)
                }
                //gender
                Spacer(Modifier.height(20.dp))
                FilterText(text = stringResource(R.string.gender))

                Spacer(Modifier.height(8.dp))
                GenderFilterRow(
                    filterState = filterState,
                ) {
                    filtersViewModel.setGender(it)
                }
                Spacer(Modifier.height(38.dp))
                DefaultButton(
                    text = "Применить",
                    onClick = {
                        goToSearchScreen()
                    }
                )
                Spacer(Modifier.height(12.dp))
                DefaultButton(
                    text = "Очистить фильтры",
                    containerColor = deleteButtonColor,
                    textColor = deleteButtonText,
                    onClick = {
                        filtersViewModel.clearFilters()
                    }
                )
            }
        }
    }
}


@Composable
fun TypeOfPetRow(
    modifier: Modifier = Modifier,
    filterState: FilterDto,
    petList: List<Int> = listOf(
        0,
        1,
        2
    ),
    onSelect: (Int) -> Unit,
) {
    LazyRow(modifier = modifier) {
        items(petList) { item ->
            val isSelected = item == filterState.typeOfPet
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color = if (isSelected) {
                            filterItemColor
                        } else {
                            districtDropDownMenuColor
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .clickable {
                        onSelect(item)
                    }
            ) {
                Text(
                    text = when (item) {
                        0 -> "Кот"
                        1 -> "Собака"
                        else -> {
                            "Другое"
                        }
                    },
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
fun TimeFilterRow(
    modifier: Modifier = Modifier,
    timeList: List<TimeFilter> = listOf(),
    filterState: FilterDto,
    onSelect: (TimeFilter) -> Unit
) {
    LazyRow(modifier = modifier) {
        items(timeList) { item ->
            val isSelected = item == filterState.time
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color = if (isSelected) {
                            filterItemColor
                        } else {
                            districtDropDownMenuColor
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .clickable {
                        onSelect(item)
                    }
            ) {
                Text(
                    text = when (
                        item
                    ) {
                        TimeFilter.TODAY -> "За сутки"
                        TimeFilter.YESTERDAY -> "Вчера"
                        TimeFilter.THREE_DAYS -> "3 дня"
                        TimeFilter.WEEK -> "Неделя"
                        TimeFilter.MONTH -> "Месяц"
                    },
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}


@Composable
fun GenderFilterRow(
    modifier: Modifier = Modifier,
    filterState: FilterDto,
    genderList: List<Int> = listOf(
        0,
        1,
    ),
    onSelect: (Int) -> Unit,
) {
    LazyRow(modifier = modifier) {
        items(genderList) { item ->
            val isSelected = item == filterState.gender
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color = if (isSelected) {
                            filterItemColor
                        } else {
                            districtDropDownMenuColor
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .clickable {
                        onSelect(item)
                    }
            ) {
                Text(
                    text = when (item) {
                        0 -> "Самец"
                        1 -> "Самка"
                        else -> {
                            ""
                        }
                    }
                )
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRadiusSlider(
    radius: Int?,
    onRadiusChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRadius = radius?.toFloat() ?: 5f

    val sliderColors = SliderDefaults.colors(
        thumbColor = deleteButtonColor,
        activeTrackColor = deleteButtonColor.copy(alpha = 0.55f),
        inactiveTrackColor = Color(0xFFE8E8E8),
        activeTickColor = Color.Transparent,
        inactiveTickColor = Color.Transparent
    )

    Column(modifier = modifier.fillMaxWidth()) {
        FilterText(text = stringResource(R.string.radius))
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "${currentRadius.toInt()} км",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E1E1E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = currentRadius,
                onValueChange = { value ->
                    onRadiusChange(value.toInt())
                },
                valueRange = 1f..20f,
                steps = 18,
                colors = sliderColors,
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(deleteButtonColor)
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        colors = sliderColors,
                        drawStopIndicator = null,
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(6.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "1 км",
                    fontSize = 18.sp,
                    color = Color(0xFF1E1E1E)
                )

                Text(
                    text = "20 км",
                    fontSize = 18.sp,
                    color = Color(0xFF1E1E1E)
                )
            }
        }
    }
}


@Preview
@Composable
private fun SearchRadiusSliderPreview() {
    SearchRadiusSlider(
        radius = 5,
        onRadiusChange = {

        },
    )
}
