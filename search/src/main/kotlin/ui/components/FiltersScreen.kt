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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.viewModel.FilterViewModel
import ui.components.default_component.DefaultButton
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import domain.models.FilterDto
import ui.models.TimeFilter
import ui.theme.buttonPrimary
import ui.theme.districtDropDownMenuColor
import ui.theme.filterItemColor

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
                Text(
                    text = "Время",
                    fontSize = 16.sp
                )
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
                Text(
                    text = "Тип животного",
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(8.dp))
                TypeOfPetRow(
                    filterState = filterState,
                ) {
                    filtersViewModel.setType(it)
                }
                //gender
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Пол",
                    fontSize = 16.sp
                )
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

@Composable
fun SearchRadiusSlider(
    radius: Int?,
    onRadiusChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRadius = radius?.toFloat() ?: 5f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Радиус поиска",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF222222)
            )

            Text(
                text = "${currentRadius.toInt()} км",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = buttonPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = currentRadius,
            onValueChange = { value ->
                onRadiusChange(value.toInt())
            },
            valueRange = 1f..20f,
            steps = 18,
            colors = SliderDefaults.colors(
                thumbColor = buttonPrimary,
                activeTrackColor = buttonPrimary,
                inactiveTrackColor = Color(0xFFE0E0E0),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "1 км",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                text = "20 км",
                fontSize = 12.sp,
                color = Color.Gray
            )
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
