package data

import domain.model.MarketAllergen
import domain.model.MarketAnimalType
import domain.model.MarketCategory
import domain.model.MarketClothingDetails
import domain.model.MarketFilter
import domain.model.MarketFoodDetails
import domain.model.MarketPage
import domain.model.MarketProduct
import domain.model.MarketSeller
import domain.model.MarketSellerShort
import domain.model.MarketVariant

object MarketMockData {
    private const val PAGE_SIZE = 20

    val allergens = listOf(
        MarketAllergen(id = "allergen-chicken", name = "Курица"),
        MarketAllergen(id = "allergen-wheat", name = "Пшеница"),
        MarketAllergen(id = "allergen-fish", name = "Рыба")
    )

    private val sellers = listOf(
        MarketSeller(
            id = "seller-groom",
            brandName = "Ателло",
            description = "Стрижки собак и кошек, экспресс-линька, уход за когтями и аккуратный груминг рядом с домом.",
            sellerType = "Дизайнер",
            logoUrl = null,
            websiteUrl = "https://example.com/atello",
            email = "hello@atello.ru",
            phone = "+7 999 123-45-67",
            city = "Красноярск",
            verified = true
        ),
        MarketSeller(
            id = "seller-food",
            brandName = "Ладо Кецховели 93",
            description = "Небольшая локальная витрина кормов, лакомств и вещей для ежедневного ухода за питомцами.",
            sellerType = "Производитель",
            logoUrl = null,
            websiteUrl = "https://example.com/lado-pets",
            email = "market@pets.ru",
            phone = "+7 999 765-43-21",
            city = "Красноярск",
            verified = true
        )
    )

    private val sellerGroomShort = sellers[0].toShort()
    private val sellerFoodShort = sellers[1].toShort()

    val products = listOf(
        MarketProduct(
            id = "service-dog-grooming",
            category = MarketCategory.Clothing,
            name = "Стрижка собаки",
            description = "Комплексная стрижка, мытьё, сушка и оформление лап. Подойдёт для маленьких и средних пород.",
            animalType = MarketAnimalType.Dog,
            price = 1990.0,
            currency = "RUB",
            externalUrl = "https://example.com/dog-grooming",
            images = emptyList(),
            variants = listOf(
                MarketVariant("variant-small", "Маленькая порода", 1990.0, true),
                MarketVariant("variant-medium", "Средняя порода", 2490.0, true)
            ),
            foodDetails = null,
            clothingDetails = MarketClothingDetails(
                material = "Услуга груминга",
                season = "Любой сезон",
                handmade = true,
                careInstructions = "После процедуры желательно не купать питомца 24 часа.",
                sizeChartUrl = null
            ),
            seller = sellerGroomShort
        ),
        MarketProduct(
            id = "service-cat-grooming",
            category = MarketCategory.Clothing,
            name = "Стрижка кошки",
            description = "Бережная стрижка кошки без лишнего стресса, уход за шерстью и когтями.",
            animalType = MarketAnimalType.Cat,
            price = 2990.0,
            currency = "RUB",
            externalUrl = "https://example.com/cat-grooming",
            images = emptyList(),
            variants = listOf(
                MarketVariant("variant-basic", "Базовый уход", 2990.0, true),
                MarketVariant("variant-care", "Уход + когти", 3490.0, true)
            ),
            foodDetails = null,
            clothingDetails = MarketClothingDetails(
                material = "Услуга груминга",
                season = "Любой сезон",
                handmade = true,
                careInstructions = "Мастер подскажет уход по типу шерсти после процедуры.",
                sizeChartUrl = null
            ),
            seller = sellerGroomShort
        ),
        MarketProduct(
            id = "collar-sand",
            category = MarketCategory.Clothing,
            name = "Ошейник для собак средних и крупных пород",
            description = "Прочный бежевый ошейник с надёжной застёжкой и металлическим кольцом для поводка.",
            animalType = MarketAnimalType.Dog,
            price = 499.0,
            currency = "RUB",
            externalUrl = "https://example.com/collar-sand",
            images = emptyList(),
            variants = listOf(
                MarketVariant("variant-m", "M", 499.0, true),
                MarketVariant("variant-l", "L", 549.0, true),
                MarketVariant("variant-xl", "XL", 599.0, false)
            ),
            foodDetails = null,
            clothingDetails = MarketClothingDetails(
                material = "Нейлон, металл, пластиковая застёжка",
                season = "Любой сезон",
                handmade = false,
                careInstructions = "Протирать влажной салфеткой, не сушить на батарее.",
                sizeChartUrl = "https://example.com/collar-sizes"
            ),
            seller = sellerFoodShort
        ),
        MarketProduct(
            id = "food-lamb-puppy",
            category = MarketCategory.Food,
            name = "Холистик для щенков с ягнёнком",
            description = "Полнорационный сухой корм из свежего мяса для щенков и молодых собак.",
            animalType = MarketAnimalType.Dog,
            price = 2490.0,
            currency = "RUB",
            externalUrl = "https://example.com/lamb-puppy",
            images = emptyList(),
            variants = listOf(
                MarketVariant("variant-2kg", "2 кг", 2490.0, true),
                MarketVariant("variant-10kg", "10 кг", 9900.0, true)
            ),
            foodDetails = MarketFoodDetails(
                composition = "Ягнёнок, рис, льняное семя, овощи, витамины и минералы.",
                foodType = "Сухой",
                ageCategory = "Для малышей",
                proteinPercent = 28.0,
                fatPercent = 16.0,
                fiberPercent = 3.0,
                moisturePercent = 9.0,
                feedingGuide = "Подбирать порцию по весу и активности питомца.",
                allergens = emptyList()
            ),
            clothingDetails = null,
            seller = sellerFoodShort
        ),
        MarketProduct(
            id = "treats-salmon",
            category = MarketCategory.Food,
            name = "Лакомства с лососем",
            description = "Мягкие кусочки для дрессировки и прогулок, удобно брать с собой.",
            animalType = MarketAnimalType.Cat,
            price = 390.0,
            currency = "RUB",
            externalUrl = "https://example.com/salmon-treats",
            images = emptyList(),
            variants = listOf(
                MarketVariant("variant-80g", "80 г", 390.0, true),
                MarketVariant("variant-200g", "200 г", 790.0, true)
            ),
            foodDetails = MarketFoodDetails(
                composition = "Лосось, батат, рисовая мука, витамины.",
                foodType = "Лакомства",
                ageCategory = "Любой возраст",
                proteinPercent = 24.0,
                fatPercent = 12.0,
                fiberPercent = 2.0,
                moisturePercent = 10.0,
                feedingGuide = "Давать как поощрение, не заменяет основной рацион.",
                allergens = listOf(allergens[2])
            ),
            clothingDetails = null,
            seller = sellerFoodShort
        ),
        MarketProduct(
            id = "winter-alaska",
            category = MarketCategory.Clothing,
            name = "Зимний комбинезон Аляска",
            description = "Тёплый комбинезон для прогулок в мороз: мембрана, мягкий утеплитель и удобная посадка.",
            animalType = MarketAnimalType.Dog,
            price = 5900.0,
            currency = "RUB",
            externalUrl = "https://example.com/alaska",
            images = emptyList(),
            variants = listOf(
                MarketVariant("variant-xs", "XS", 5900.0, true),
                MarketVariant("variant-s", "S", 5900.0, true),
                MarketVariant("variant-m", "M", 6400.0, true)
            ),
            foodDetails = null,
            clothingDetails = MarketClothingDetails(
                material = "Мембрана 10К, синтепух, мягкая подкладка",
                season = "Зима",
                handmade = true,
                careInstructions = "Стирка при 30°C, без отжима.",
                sizeChartUrl = "https://example.com/alaska-sizes"
            ),
            seller = sellerGroomShort
        )
    )

    fun products(filter: MarketFilter, page: Int): MarketPage<MarketProduct> {
        val filtered = products
            .filter { product -> filter.category == null || product.category == filter.category }
            .filter { product -> filter.animalType == null || product.animalType == filter.animalType }
            .filter { product ->
                val query = filter.search.trim()
                query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.description.orEmpty().contains(query, ignoreCase = true) ||
                    product.seller.brandName.contains(query, ignoreCase = true)
            }
            .filter { product ->
                filter.excludeAllergenIds.isEmpty() ||
                    product.foodDetails?.allergens.orEmpty().none { allergen ->
                        allergen.id in filter.excludeAllergenIds
                    }
            }

        val fromIndex = page * PAGE_SIZE
        val pageItems = filtered.drop(fromIndex).take(PAGE_SIZE)

        return MarketPage(
            items = pageItems,
            page = page,
            isLast = fromIndex + PAGE_SIZE >= filtered.size
        )
    }

    fun product(productId: String): MarketProduct? = products.firstOrNull { it.id == productId }

    fun seller(sellerId: String): MarketSeller? = sellers.firstOrNull { it.id == sellerId }

    fun sellerProducts(sellerId: String, page: Int): MarketPage<MarketProduct> {
        val sellerProducts = products.filter { it.seller.id == sellerId }
        val fromIndex = page * PAGE_SIZE

        return MarketPage(
            items = sellerProducts.drop(fromIndex).take(PAGE_SIZE),
            page = page,
            isLast = fromIndex + PAGE_SIZE >= sellerProducts.size
        )
    }

    private fun MarketSeller.toShort() = MarketSellerShort(
        id = id,
        brandName = brandName,
        logoUrl = logoUrl,
        websiteUrl = websiteUrl,
        verified = verified
    )
}
