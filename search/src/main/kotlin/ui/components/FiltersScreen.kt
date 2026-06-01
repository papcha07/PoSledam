package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.viewModel.FilterViewModel
import ui.components.default_component.DefaultButton
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import domain.models.FilterDto
import ui.models.TimeFilter
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
                SelectedDistrict(
                    currentDistrict = filterState.district,
                    onSelectDistrict = { district ->
                        filtersViewModel.setDistrict(district)
                    }
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
                        filtersViewModel.findFoundPets()
                        filtersViewModel.findMissingPets()
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedDistrict(
    modifier: Modifier = Modifier,
    currentDistrict: String?,                // <- текущее значение из VM
    onSelectDistrict: (String?) -> Unit      // <- сообщаем наверх
) {
    val districts = remember {
        listOf(
            "Не выбрано",
            "Октябрьский район",
            "Железнодорожный район",
            "Кировский район",
            "Ленинский район",
            "Свердловский район",
            "Советский район",
            "Центральный район"
        )
    }

    val selectedText = currentDistrict ?: districts.first()

    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier
            .padding(top = 10.dp)
    ) {
        Text(
            text = "Где искать",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = districtDropDownMenuColor,
                    focusedContainerColor = districtDropDownMenuColor,
                    disabledContainerColor = districtDropDownMenuColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                districts.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            expanded = false
                            onSelectDistrict(
                                if (item == "Не выбрано") null else item
                            )
                        }
                    )
                }
            }
        }
    }
}
