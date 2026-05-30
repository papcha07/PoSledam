package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import domain.models.PetUiPreview
import ui.components.default_component.TabRowSelection
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.components.placeholder.ErrorPlaceholder
import ui.model.TabRowInfo
import ui.models.SearchState
import ui.theme.addressSearchColor
import ui.theme.backgroundColor
import ui.theme.filterItemColor
import ui.viewModel.FilterChipUi
import ui.viewModel.FilterViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    filtersViewModel: FilterViewModel,
    onActionClick: () -> Unit,
    goToDetailsPetScreen: (String, Int) -> Unit
) {

    val chipsState by filtersViewModel.chips.collectAsState()
    val currentTab by filtersViewModel.currentTab.collectAsState()

    Column(
        Modifier.background(color = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                Column(
                    Modifier
                        .background(color = Color.White)
                        .padding(bottom = 12.dp)
                ) {
                    ToolBar(
                        toolBarInfo = ToolBarInfo(
                            title = "Поиск питомцев",
                            backArrow = false,
                            nextRoute = "",
                            prevRoute = "",
                            backArrowIcon = null,
                            actionIcon = R.drawable.ic_filter,
                        ),
                        onActionClick = { onActionClick() }
                    )

                    TabRowSelection(
                        listOfTabInfo = listOf(
                            TabRowInfo("Найденные"),
                            TabRowInfo("Пропажи")
                        ),
                        selectedTabIndex = currentTab,
                        onTabSelected = { tabIndex ->
                            filtersViewModel.setCurrentTab(tabIndex)
                        }
                    )
                }

                Spacer(Modifier.height(4.dp))

                FiltersBar(chips = chipsState)

                Spacer(Modifier.height(4.dp))

                when (currentTab) {
                    0 -> FoundPetsScreen(
                        viewModel = filtersViewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        goToDetailsPetScreen = goToDetailsPetScreen
                    )

                    1 -> MissingPetsScreen(
                        viewModel = filtersViewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        goToDetailsPetScreen = goToDetailsPetScreen
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
fun FiltersBar(
    chips: List<FilterChipUi>,
) {
    if (chips.isEmpty()) return
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(color = Color.White)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            val districtText = chips.firstOrNull() {
                it.key == "district"
            }?.text ?: ""

            Row {
                Image(
                    painter = painterResource(R.drawable.ic_search_geo),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text("Красноярск ${districtText}")
            }
            Spacer(Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(chips, key = { it.key }) { chip ->
                    FilterChipItem(
                        text = chip.text,
                    )
                }
            }
        }

    }

}

@Preview
@Composable
private fun FiltersBarPreview(
) {
    FiltersBar(
        chips = listOf(
            FilterChipUi(
                key = "gender",
                text = "Женский"
            ),
            FilterChipUi(
                key = "gender",
                text = "Собака"
            ),
            FilterChipUi(
                key = "gender",
                text = "Неделя"
            ),
        )
    )
}

@Composable
fun FilterChipItem(
    text: String,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = filterItemColor)
            .border(1.dp, Color(0xFFE0DAFF), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = Color.Black)
    }
}

@Preview
@Composable
private fun FilterChipItemPreview() {
    FilterChipItem(
        text = "Собака"
    )
}

@Composable
fun PetCardComponent(
    modifier: Modifier = Modifier,
    petInfo: PetUiPreview,
    filterViewModel: FilterViewModel,
    goToDetailsPetScreen: () -> Unit
) {

    val currentTab = filterViewModel.currentTab.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(15.dp),
                clip = false,
                spotColor = Color.Black.copy(alpha = 0.35f),
                ambientColor = Color.Black.copy(alpha = 0.35f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .clip(RoundedCornerShape(15.dp))
            .clickable {
                goToDetailsPetScreen()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Box {
                if (petInfo.imageUrl != null) {
                    val imageUrl = "$BASE_URL/api/image/${petInfo.imageUrl}"
                    println("Loading image from: $imageUrl")
                    AsyncImage(
                        modifier = Modifier
                            .size(130.dp, 140.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        model = petInfo.imageUrl,
                        placeholder = painterResource(R.drawable.ic_dog),
                        error = painterResource(R.drawable.ic_dog),
                        contentDescription = null,
                        onError = {
                            println("Image loading failed: ${it.result.throwable?.message}")
                            it.result.throwable?.printStackTrace()
                        },
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color = if (currentTab.value == 1) Color.Red else Color.Green)
                            .padding(10.dp)
                    )
                }

            }

            Spacer(Modifier.width(14.dp))

            Box(
                modifier = Modifier.height(140.dp)
            ) {
                Column(
                    Modifier.padding(top = 16.dp)
                ) {

                    Text(
                        text = petInfo.petName ?: petInfo.breed ?: "",
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (petInfo.description.length > 110) {
                            "${
                                petInfo.description.substring(
                                    0,
                                    petInfo.description.length - 30
                                )
                            }..."
                        } else {
                            petInfo.description
                        },

                        fontSize = 12.sp
                    )
                }
                Text(
                    modifier = Modifier.align(Alignment.BottomStart),
                    text = petInfo.district?.toUpperCase() ?: "Нет района",
                    fontSize = 14.sp,
                    color = addressSearchColor
                )
            }

        }
    }
}

@Composable
fun FoundPetsScreen(
    viewModel: FilterViewModel,
    modifier: Modifier = Modifier,
    goToDetailsPetScreen: (String, Int) -> Unit
) {


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White),
    ) {
        when (val state = foundState) {
            is SearchState.Idle -> {}

            is SearchState.Loading -> {
                Box(Modifier.align(Alignment.Center)) {
                    CircularProgressIndicator()
                }
            }

            is SearchState.Success -> {
                if (searchResults.isNotEmpty()) {
                    PetsList(
                        pets = searchResults,
                        viewModel = viewModel,
                        goToDetailsPetScreen = goToDetailsPetScreen,
                        isFoundTab = true
                    )
                } else {
                    Box(Modifier.align(Alignment.Center)) {
                        Text("Нет найденных питомцев")
                    }
                }
            }

            is SearchState.Error -> {
                ErrorPlaceholder(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
fun MissingPetsScreen(
    viewModel: FilterViewModel,
    modifier: Modifier = Modifier,
    goToDetailsPetScreen: (String, Int) -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White),
    ) {
        when (val state = missingState) {
            is SearchState.Idle -> {}

            is SearchState.Loading -> {
                Box(Modifier.align(Alignment.Center)) {
                    CircularProgressIndicator()
                }
            }

            is SearchState.Success -> {
                if (searchResults.isNotEmpty()) {
                    PetsList(
                        pets = searchResults,
                        viewModel = viewModel,
                        goToDetailsPetScreen = goToDetailsPetScreen,
                        isFoundTab = false
                    )
                } else {
                    Box(Modifier.align(Alignment.Center)) {
                        Text("Нет найденных питомцев")
                    }
                }
            }

            is SearchState.Error -> {
                ErrorPlaceholder(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
fun PetsList(
    pets: List<PetUiPreview>,
    modifier: Modifier = Modifier,
    viewModel: FilterViewModel,
    goToDetailsPetScreen: (String, Int) -> Unit,
    isFoundTab: Boolean
) {
    val currentTabCategory = viewModel.currentTab.collectAsState()
    val hasMore =
        if (isFoundTab) viewModel.hasMoreFound.collectAsState() else viewModel.hasMoreMissing.collectAsState()
    val isLoadingMore =
        if (isFoundTab) viewModel.isLoadingMoreFound.collectAsState() else viewModel.isLoadingMoreMissing.collectAsState()

    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        state = listState
    ) {
        items(pets, key = { it.id }) { petInfo ->
            PetCardComponent(petInfo = petInfo, filterViewModel = viewModel) {
                goToDetailsPetScreen(petInfo.id, currentTabCategory.value)
            }
            Spacer(Modifier.height(8.dp))
        }
        if (isLoadingMore.value) {
            item(key = "loading_more") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

