package ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import domain.model.MarketAnimalType
import domain.model.MarketCategory
import domain.model.MarketClothingDetails
import domain.model.MarketFoodDetails
import domain.model.MarketProduct
import domain.model.MarketSeller
import domain.model.MarketVariant
import ui.components.default_component.ToolBar
import ui.components.default_component.ToolBarInfo
import ui.model.MarketProductDetailsUiState
import ui.model.MarketSellerUiState
import ui.theme.PurpleButtonColor
import ui.theme.backgroundColor
import ui.theme.buttonSecondPrimary
import ui.theme.textHint
import ui.viewModel.MarketCatalogViewModel
import ui.viewModel.MarketProductDetailsViewModel
import ui.viewModel.MarketSellerViewModel

@Composable
fun MarketScreen(
    viewModel: MarketCatalogViewModel,
    openProduct: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val gridState = rememberLazyGridState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= state.products.lastIndex - 4 && state.products.isNotEmpty()
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .padding(bottom = 12.dp)
        ) {
            ToolBar(
                toolBarInfo = ToolBarInfo(
                    title = "Маркет",
                    backArrow = false
                )
            )

            MarketSearchField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                onSubmit = viewModel::submitSearch
            )

            Spacer(Modifier.height(12.dp))

            CategoryChips(
                selectedCategory = state.selectedCategory,
                onSelect = viewModel::selectCategory
            )

            Spacer(Modifier.height(10.dp))

            AnimalFilterChips(
                selectedAnimal = state.selectedAnimalType,
                withoutAllergens = state.withoutAllergens,
                onAnimalSelect = viewModel::selectAnimalType,
                onWithoutAllergensClick = viewModel::toggleWithoutAllergens
            )
        }

        if (state.errorMessage != null && state.products.isEmpty()) {
            MarketMessage(
                modifier = Modifier.weight(1f),
                title = state.errorMessage ?: "Ошибка",
                actionText = "Повторить",
                onAction = viewModel::refresh
            )
        } else if (state.isLoading) {
            MarketLoading(modifier = Modifier.weight(1f))
        } else if (state.products.isEmpty()) {
            MarketMessage(
                modifier = Modifier.weight(1f),
                title = "Пока нет товаров",
                actionText = "Обновить",
                onAction = viewModel::refresh
            )
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                state = gridState,
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 20.dp)
            ) {
                itemsIndexed(
                    items = state.products,
                    key = { _, product -> product.id }
                ) { _, product ->
                    MarketProductGridCard(
                        product = product,
                        onClick = { openProduct(product.id) }
                    )
                }

                if (state.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = PurpleButtonColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketProductDetailsScreen(
    productId: String,
    viewModel: MarketProductDetailsViewModel,
    onBack: () -> Unit,
    openSeller: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.load(productId)
    }

    when (val currentState = state) {
        is MarketProductDetailsUiState.Loading -> MarketLoading(modifier = Modifier.fillMaxSize())
        is MarketProductDetailsUiState.Error -> MarketMessage(
            modifier = Modifier.fillMaxSize(),
            title = currentState.message,
            actionText = "Назад",
            onAction = onBack
        )

        is MarketProductDetailsUiState.Success -> {
            val product = currentState.product
            Scaffold(
                containerColor = backgroundColor,
                bottomBar = {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(52.dp),
                        enabled = !product.externalUrl.isNullOrBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleButtonColor),
                        shape = RoundedCornerShape(18.dp),
                        onClick = { product.externalUrl?.let { openExternalUrl(context, it) } }
                    ) {
                        Text("Перейти к покупке", color = Color.White, fontSize = 15.sp)
                    }
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    item {
                        ProductHero(
                            product = product,
                            onBack = onBack
                        )
                    }

                    item {
                        ProductMainInfo(
                            product = product,
                            openSeller = openSeller
                        )
                    }

                    if (product.variants.isNotEmpty()) {
                        item {
                            ProductSection(title = "Варианты") {
                                VariantRow(product.variants)
                            }
                        }
                    }

                    product.foodDetails?.let { foodDetails ->
                        item {
                            FoodDetailsSection(foodDetails)
                        }
                    }

                    product.clothingDetails?.let { clothingDetails ->
                        item {
                            ClothingDetailsSection(clothingDetails)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketSellerScreen(
    sellerId: String,
    viewModel: MarketSellerViewModel,
    onBack: () -> Unit,
    openProduct: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(sellerId) {
        viewModel.load(sellerId)
    }

    when {
        state.isLoading -> MarketLoading(modifier = Modifier.fillMaxSize())
        state.errorMessage != null -> MarketMessage(
            modifier = Modifier.fillMaxSize(),
            title = state.errorMessage ?: "Ошибка",
            actionText = "Назад",
            onAction = onBack
        )

        state.seller != null -> {
            val seller = state.seller ?: return
            SellerContent(
                state = state,
                onBack = onBack,
                openProduct = openProduct,
                openSite = { seller.websiteUrl?.let { openExternalUrl(context, it) } },
                loadMore = viewModel::loadNextPage
            )
        }
    }
}

@Composable
private fun MarketSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(54.dp),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        placeholder = { Text("Найти корм, одежду или бренд", color = textHint, fontSize = 13.sp) },
        trailingIcon = {
            IconButton(onClick = onSubmit) {
                Icon(
                    painter = painterResource(R.drawable.ic_search_bottom),
                    contentDescription = "Найти",
                    tint = PurpleButtonColor
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF8F8F8),
            unfocusedContainerColor = Color(0xFFF8F8F8),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun CategoryChips(
    selectedCategory: MarketCategory?,
    onSelect: (MarketCategory?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            MarketChip(
                text = "Все",
                selected = selectedCategory == null,
                onClick = { onSelect(null) }
            )
        }
        items(MarketCategory.entries) { category ->
            MarketChip(
                text = category.title,
                selected = selectedCategory == category,
                onClick = { onSelect(category) }
            )
        }
    }
}

@Composable
private fun AnimalFilterChips(
    selectedAnimal: MarketAnimalType?,
    withoutAllergens: Boolean,
    onAnimalSelect: (MarketAnimalType?) -> Unit,
    onWithoutAllergensClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            MarketChip(
                text = "Все питомцы",
                selected = selectedAnimal == null,
                onClick = { onAnimalSelect(null) }
            )
        }
        items(listOf(MarketAnimalType.Cat, MarketAnimalType.Dog, MarketAnimalType.Other)) { animal ->
            MarketChip(
                text = animal.title,
                selected = selectedAnimal == animal,
                onClick = { onAnimalSelect(animal) }
            )
        }
        item {
            MarketChip(
                text = "Без аллергенов",
                selected = withoutAllergens,
                onClick = onWithoutAllergensClick
            )
        }
    }
}

@Composable
private fun MarketChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 13.sp,
                color = if (selected) Color.White else Color.Black
            )
        },
        shape = RoundedCornerShape(14.dp),
        border = FilterChipDefaults.filterChipBorder(
            selected = selected,
            enabled = true,
            borderColor = Color.Transparent,
            selectedBorderColor = Color.Transparent
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color(0xFFF8F8F8),
            selectedContainerColor = PurpleButtonColor
        )
    )
}

@Composable
private fun MarketProductGridCard(
    product: MarketProduct,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            ProductImage(
                imageUrl = product.images.firstOrNull(),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = product.priceText(),
                color = PurpleButtonColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = product.name,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            SellerLine(product.seller.brandName, product.seller.verified)
        }
    }
}

@Composable
private fun ProductHero(
    product: MarketProduct,
    onBack: () -> Unit
) {
    Box {
        ProductImage(
            imageUrl = product.images.firstOrNull(),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentScale = ContentScale.Crop
        )

        IconButton(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(42.dp)
                .background(Color.White.copy(alpha = 0.78f), CircleShape)
                .align(Alignment.TopStart),
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(R.drawable.left_arrow),
                contentDescription = "Назад"
            )
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmallPill(product.category.title)
            SmallPill(product.animalType.title)
        }
    }
}

@Composable
private fun ProductMainInfo(
    product: MarketProduct,
    openSeller: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = product.priceText(),
            color = PurpleButtonColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(text = product.name, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        product.description?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(10.dp))
            Text(text = it, fontSize = 14.sp, color = Color(0xFF3A3A3A), lineHeight = 19.sp)
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8F8F8))
                .clickable { openSeller(product.seller.id) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SellerLogo(url = product.seller.logoUrl, size = 42)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                SellerLine(product.seller.brandName, product.seller.verified)
                Text("Продавец", color = textHint, fontSize = 12.sp)
            }
            Icon(
                painter = painterResource(R.drawable.right_arrow),
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun ProductSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(18.dp))
                .padding(14.dp),
            content = content
        )
    }
}

@Composable
private fun VariantRow(variants: List<MarketVariant>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(variants) { variant ->
            Column(
                modifier = Modifier
                    .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(variant.label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                variant.price?.let {
                    Text(formatPrice(it, "RUB"), color = PurpleButtonColor, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun FoodDetailsSection(details: MarketFoodDetails) {
    ProductSection(title = "О корме") {
        DetailLine("Тип", details.foodType)
        DetailLine("Возраст", details.ageCategory)
        DetailLine("Состав", details.composition)
        details.feedingGuide?.let { DetailLine("Рекомендации", it) }
        PercentLine("Протеин", details.proteinPercent)
        PercentLine("Жир", details.fatPercent)
        PercentLine("Клетчатка", details.fiberPercent)
        PercentLine("Влага", details.moisturePercent)
        if (details.allergens.isNotEmpty()) {
            DetailLine("Аллергены", details.allergens.joinToString { it.name })
        }
    }
}

@Composable
private fun ClothingDetailsSection(details: MarketClothingDetails) {
    ProductSection(title = "Об одежде") {
        DetailLine("Материал", details.material)
        DetailLine("Сезон", details.season)
        DetailLine("Ручная работа", if (details.handmade) "Да" else "Нет")
        details.careInstructions?.let { DetailLine("Уход", it) }
        details.sizeChartUrl?.let { DetailLine("Размерная сетка", it) }
    }
}

@Composable
private fun SellerContent(
    state: MarketSellerUiState,
    onBack: () -> Unit,
    openProduct: (String) -> Unit,
    openSite: () -> Unit,
    loadMore: () -> Unit
) {
    val seller = state.seller ?: return
    var selectedTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            ToolBar(
                toolBarInfo = ToolBarInfo(
                    title = "Продавец",
                    backArrow = true,
                    backArrowIcon = R.drawable.left_arrow
                ),
                onBackClick = onBack
            )
        }

        item {
            SellerHeader(
                seller = seller,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                openSite = openSite
            )
        }

        item {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 18.dp, bottom = 10.dp),
                text = if (selectedTab == 2) "Прайс" else "Товары",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(state.products, key = { it.id }) { product ->
            MarketProductListCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                product = product,
                onClick = { openProduct(product.id) }
            )
        }

        item {
            LaunchedEffect(state.products.size, state.isLastPage) {
                if (state.products.isNotEmpty() && !state.isLastPage) loadMore()
            }
            if (state.isLoadingMore) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurpleButtonColor)
                }
            }
        }
    }
}

@Composable
private fun SellerHeader(
    seller: MarketSeller,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    openSite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SellerLogo(url = seller.logoUrl, size = 58)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                SellerLine(name = seller.brandName, verified = seller.verified, fontSize = 22)
                Text(seller.sellerType, color = textHint, fontSize = 13.sp)
            }
        }

        seller.description?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(14.dp))
            Text(text = it, fontSize = 14.sp, lineHeight = 19.sp, color = Color(0xFF3A3A3A))
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("О компании", "Адрес", "Прайс").forEachIndexed { index, title ->
                MarketChip(
                    text = title,
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        when (selectedTab) {
            0 -> SellerAbout(seller = seller, openSite = openSite)
            1 -> DetailLine("Город", seller.city ?: "Не указан")
            2 -> Text("Актуальные позиции продавца ниже", fontSize = 14.sp)
        }
    }
}

@Composable
private fun SellerAbout(
    seller: MarketSeller,
    openSite: () -> Unit
) {
    seller.email?.let { DetailLine("Email", it) }
    seller.phone?.let { DetailLine("Телефон", it) }
    if (!seller.websiteUrl.isNullOrBlank()) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(48.dp),
            onClick = openSite,
            colors = ButtonDefaults.buttonColors(containerColor = PurpleButtonColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Сайт продавца", color = Color.White)
        }
    }
}

@Composable
private fun MarketProductListCard(
    modifier: Modifier = Modifier,
    product: MarketProduct,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            ProductImage(
                imageUrl = product.images.firstOrNull(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(178.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(Modifier.height(12.dp))
            Text(product.priceText(), color = PurpleButtonColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(product.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(product.seller.brandName, color = Color(0xFF3A3A3A), fontSize = 13.sp)
        }
    }
}

@Composable
private fun ProductImage(
    imageUrl: String?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (imageUrl.isNullOrBlank()) {
        Box(
            modifier = modifier.background(buttonSecondPrimary),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_dog),
                contentDescription = null,
                modifier = Modifier.size(58.dp)
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
            placeholder = painterResource(R.drawable.ic_dog),
            error = painterResource(R.drawable.ic_dog)
        )
    }
}

@Composable
private fun SellerLogo(
    url: String?,
    size: Int
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(buttonSecondPrimary),
        contentAlignment = Alignment.Center
    ) {
        if (url.isNullOrBlank()) {
            Image(
                painter = painterResource(R.drawable.ic_lapa),
                contentDescription = null,
                modifier = Modifier.size((size / 2).dp)
            )
        } else {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_lapa)
            )
        }
    }
}

@Composable
private fun SellerLine(
    name: String,
    verified: Boolean,
    fontSize: Int = 12
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = name,
            fontSize = fontSize.sp,
            fontWeight = if (fontSize > 14) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (verified) {
            Spacer(Modifier.width(6.dp))
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(buttonSecondPrimary)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                text = "Проверен",
                color = PurpleButtonColor,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SmallPill(text: String) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(alpha = 0.42f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        text = text,
        color = Color.White,
        fontSize = 12.sp
    )
}

@Composable
private fun DetailLine(label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = textHint, fontSize = 12.sp)
        Text(value, color = Color.Black, fontSize = 14.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun PercentLine(label: String, value: Double?) {
    value?.let { DetailLine(label, "${trimPrice(it)}%") }
}

@Composable
private fun MarketLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PurpleButtonColor)
    }
}

@Composable
private fun MarketMessage(
    modifier: Modifier = Modifier,
    title: String,
    actionText: String,
    onAction: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = PurpleButtonColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(actionText, color = Color.White)
            }
        }
    }
}

private fun MarketProduct.priceText(): String = price?.let { formatPrice(it, currency) } ?: "Цена по ссылке"

private fun formatPrice(price: Double, currency: String): String {
    val sign = if (currency == "RUB") "Р" else currency
    return "${trimPrice(price)} $sign"
}

private fun trimPrice(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.1f", value)
}

private fun openExternalUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
