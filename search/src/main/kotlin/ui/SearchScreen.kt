package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.core.R
import com.example.search.R as SearchR
import domain.models.PetUiPreview
import ui.components.default_component.TabRowSelection
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.components.placeholder.ErrorPlaceholder
import ui.components.placeholder.NotFoundSearchPlaceholder
import ui.components.placeholder.ShimmerImagePlaceholder
import ui.components.placeholder.ShimmerLoadingTransition
import ui.components.placeholder.ShimmerTextPlaceholder
import ui.model.TabRowInfo
import ui.theme.addressSearchColor
import ui.theme.backgroundColor
import ui.theme.deleteButtonColor
import ui.theme.filterItemColor
import ui.theme.textHint
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
            .background(color = deleteButtonColor)
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
    isMissing: Boolean,
    goToDetailsPetScreen: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)

    val title = petInfo.petName
        ?: petInfo.breed
        ?: stringResource(SearchR.string.search_pet_no_information)

    val description = petInfo.description
        ?: stringResource(SearchR.string.search_pet_no_description)

    val district = petInfo.district?.toSearchDistrictLabel()
        ?: stringResource(SearchR.string.search_pet_no_district).uppercase()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                onClick = goToDetailsPetScreen
            )
            .padding(4.dp)
    ){

        Box {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(shape),
                model = petInfo.imageUrl,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_dog),
                error = painterResource(R.drawable.ic_dog),
                fallback = painterResource(R.drawable.ic_dog),
                contentDescription = null
            )

            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(if (isMissing) Color.Red else Color.Green)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = description,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            color = textHint
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = district,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = addressSearchColor
        )
    }
}

private fun String.toSearchDistrictLabel(): String {
    return replace("административный район", "", ignoreCase = true)
        .replace("район", "", ignoreCase = true)
        .trim(' ', ',', '.', '-')
        .uppercase()
}

@Preview(name = "320 dp", widthDp = 320, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(name = "360 dp", widthDp = 360, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(name = "393 dp", widthDp = 393, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(name = "412 dp", widthDp = 412, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PetCardComponentPreview() {
    val pet = PetUiPreview(
        id = "preview-id",
        petName = "Очень длинное имя породистого питомца",
        description = "Длинное описание животного, которое занимает больше двух строк и должно завершиться многоточием",
        district = "Центральный административный район",
        imageUrl = null,
        breed = "Бордер-колли"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PetCardComponent(
            modifier = Modifier.weight(1f),
            petInfo = pet,
            isMissing = false,
            goToDetailsPetScreen = {}
        )
        PetCardComponent(
            modifier = Modifier.weight(1f),
            petInfo = pet.copy(id = "preview-id-2"),
            isMissing = true,
            goToDetailsPetScreen = {}
        )
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
        val pets = viewModel.findPets.collectAsLazyPagingItems()
        val refreshState = pets.loadState.refresh

        ShimmerLoadingTransition(
            isLoading = refreshState is LoadState.Loading,
            modifier = Modifier.fillMaxSize(),
            loadingContent = {
                SearchPetCardShimmerList(modifier = Modifier.fillMaxSize())
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                when (refreshState) {
                    is LoadState.Error -> {
                        ErrorPlaceholder(
                            modifier = Modifier.align(Alignment.Center),
                            onRefreshClick = pets::refresh
                        )
                    }

                    LoadState.Loading -> Unit

                    is LoadState.NotLoading -> {
                        if (pets.itemCount > 0) {
                            PetsList(
                                pets = pets,
                                goToDetailsPetScreen = goToDetailsPetScreen,
                                announcementType = FOUND_ANNOUNCEMENT_TYPE
                            )
                        } else {
                            val modifier = Modifier.align(Alignment.Center)
                            NotFoundSearchPlaceholder(
                                modifier = modifier
                            )
                        }
                    }
                }
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
    val pets = viewModel.missingPets.collectAsLazyPagingItems()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White),
    ) {
        val refreshState = pets.loadState.refresh

        ShimmerLoadingTransition(
            isLoading = refreshState is LoadState.Loading,
            modifier = Modifier.fillMaxSize(),
            loadingContent = {
                SearchPetCardShimmerList(modifier = Modifier.fillMaxSize())
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                when (refreshState) {
                    is LoadState.Loading -> Unit

                    is LoadState.Error -> {
                        ErrorPlaceholder(
                            modifier = Modifier.align(Alignment.Center),
                            onRefreshClick = pets::refresh
                        )
                    }

                    is LoadState.NotLoading -> {
                        if (pets.itemCount > 0) {
                            PetsList(
                                pets = pets,
                                goToDetailsPetScreen = goToDetailsPetScreen,
                                announcementType = MISSING_ANNOUNCEMENT_TYPE
                            )
                        } else {
                            val modifier = Modifier.align(Alignment.Center)
                            NotFoundSearchPlaceholder(
                                modifier = modifier
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PetsList(
    pets: LazyPagingItems<PetUiPreview>,
    modifier: Modifier = Modifier,
    goToDetailsPetScreen: (String, Int) -> Unit,
    announcementType: Int
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        state = gridState,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(
            count = pets.itemCount,
            key = { index ->
                pets.peek(index)?.id ?: index
            }
        ) { index ->
            val petInfo = pets[index]

            if (petInfo != null) {
                PetCardComponent(
                    modifier = Modifier.fillMaxWidth(),
                    petInfo = petInfo,
                    isMissing = announcementType == MISSING_ANNOUNCEMENT_TYPE
                ) {
                    goToDetailsPetScreen(
                        petInfo.id,
                        announcementType
                    )
                }
            }
        }

        item(
            key = "append-state",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            val appendState = pets.loadState.append
            ShimmerLoadingTransition(
                isLoading = appendState is LoadState.Loading,
                modifier = Modifier.fillMaxWidth(),
                loadingContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SearchPetCardShimmerPlaceholder(Modifier.weight(1f))
                        SearchPetCardShimmerPlaceholder(Modifier.weight(1f))
                    }
                }
            ) {
                when (appendState) {
                    is LoadState.Loading -> Unit

                    is LoadState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(SearchR.string.search_append_load_error))
                                TextButton(onClick = pets::retry) {
                                    Text(stringResource(SearchR.string.search_retry))
                                }
                            }
                        }
                    }

                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}

@Composable
private fun SearchPetCardShimmerList(
    modifier: Modifier = Modifier,
    count: Int = 6
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(count) {
            SearchPetCardShimmerPlaceholder()
        }
    }
}

@Composable
fun SearchPetCardShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ShimmerImagePlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(Modifier.height(10.dp))
        ShimmerTextPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
        )
        Spacer(Modifier.height(6.dp))
        ShimmerTextPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
        )
        Spacer(Modifier.height(4.dp))
        ShimmerTextPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(14.dp)
        )
        Spacer(Modifier.height(10.dp))
        ShimmerTextPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(16.dp)
        )
    }
}

private const val FOUND_ANNOUNCEMENT_TYPE = 0
private const val MISSING_ANNOUNCEMENT_TYPE = 1
