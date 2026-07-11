package domain.model

data class MarketPage<T>(
    val items: List<T>,
    val page: Int,
    val isLast: Boolean
)

data class MarketFilter(
    val category: MarketCategory? = null,
    val animalType: MarketAnimalType? = null,
    val search: String = "",
    val excludeAllergenIds: List<String> = emptyList()
)

enum class MarketCategory(val title: String) {
    Food("Корм"),
    Clothing("Одежда")
}

enum class MarketAnimalType(val title: String) {
    Dog("Собаки"),
    Cat("Кошки"),
    Rodent("Грызуны"),
    Bird("Птицы"),
    Other("Другое")
}

data class MarketAllergen(
    val id: String,
    val name: String
)

data class MarketSellerShort(
    val id: String,
    val brandName: String,
    val logoUrl: String?,
    val websiteUrl: String?,
    val verified: Boolean
)

data class MarketSeller(
    val id: String,
    val brandName: String,
    val description: String?,
    val sellerType: String,
    val logoUrl: String?,
    val websiteUrl: String?,
    val email: String?,
    val phone: String?,
    val city: String?,
    val verified: Boolean
)

data class MarketVariant(
    val id: String,
    val label: String,
    val price: Double?,
    val inStock: Boolean
)

data class MarketFoodDetails(
    val composition: String,
    val foodType: String,
    val ageCategory: String,
    val proteinPercent: Double?,
    val fatPercent: Double?,
    val fiberPercent: Double?,
    val moisturePercent: Double?,
    val feedingGuide: String?,
    val allergens: List<MarketAllergen>
)

data class MarketClothingDetails(
    val material: String,
    val season: String,
    val handmade: Boolean,
    val careInstructions: String?,
    val sizeChartUrl: String?
)

data class MarketProduct(
    val id: String,
    val category: MarketCategory,
    val name: String,
    val description: String?,
    val animalType: MarketAnimalType,
    val price: Double?,
    val currency: String,
    val externalUrl: String?,
    val images: List<String>,
    val variants: List<MarketVariant>,
    val foodDetails: MarketFoodDetails?,
    val clothingDetails: MarketClothingDetails?,
    val seller: MarketSellerShort
)

sealed interface MarketResult<out T> {
    data class Success<T>(val data: T) : MarketResult<T>
    data class Error(val code: Int) : MarketResult<Nothing>
}
