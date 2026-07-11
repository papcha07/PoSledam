package apiService.models.petmarket_models

import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    val content: List<T> = emptyList(),
    val number: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val last: Boolean = true,
    val first: Boolean = true,
    val empty: Boolean = content.isEmpty()
)

@Serializable
enum class SellerTypeResponse { BREEDER, MANUFACTURER, DESIGNER }

@Serializable
enum class ProductCategoryResponse { FOOD, CLOTHING }

@Serializable
enum class ProductStatusResponse { DRAFT, PUBLISHED, ARCHIVED }

@Serializable
enum class AnimalTypeResponse { DOG, CAT, RODENT, BIRD, OTHER }

@Serializable
enum class FoodTypeResponse { DRY, WET, TREATS, RAW }

@Serializable
enum class AgeCategoryResponse { PUPPY, ADULT, SENIOR, ALL }

@Serializable
enum class SeasonResponse { WINTER, DEMI, SUMMER, ALL }

@Serializable
data class SellerResponse(
    val id: String,
    val brandName: String,
    val description: String? = null,
    val sellerType: SellerTypeResponse,
    val logoUrl: String? = null,
    val websiteUrl: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val city: String? = null,
    val verified: Boolean = false,
    val createdAt: String? = null
)

@Serializable
data class SellerShortResponse(
    val id: String,
    val brandName: String,
    val logoUrl: String? = null,
    val websiteUrl: String? = null,
    val verified: Boolean = false
)

@Serializable
data class AllergenResponse(
    val id: String,
    val name: String
)

@Serializable
data class FoodDetailsResponse(
    val composition: String,
    val foodType: FoodTypeResponse,
    val ageCategory: AgeCategoryResponse,
    val proteinPercent: Double? = null,
    val fatPercent: Double? = null,
    val fiberPercent: Double? = null,
    val moisturePercent: Double? = null,
    val feedingGuide: String? = null,
    val allergens: List<AllergenResponse> = emptyList()
)

@Serializable
data class ClothingDetailsResponse(
    val material: String,
    val season: SeasonResponse,
    val handmade: Boolean = false,
    val careInstructions: String? = null,
    val sizeChartUrl: String? = null
)

@Serializable
data class VariantResponse(
    val id: String,
    val label: String,
    val price: Double? = null,
    val inStock: Boolean = true
)

@Serializable
data class ProductResponse(
    val id: String,
    val category: ProductCategoryResponse,
    val name: String,
    val description: String? = null,
    val animalType: AnimalTypeResponse,
    val price: Double? = null,
    val currency: String = "RUB",
    val externalUrl: String? = null,
    val status: ProductStatusResponse,
    val images: List<String> = emptyList(),
    val variants: List<VariantResponse> = emptyList(),
    val foodDetails: FoodDetailsResponse? = null,
    val clothingDetails: ClothingDetailsResponse? = null,
    val seller: SellerShortResponse,
    val createdAt: String? = null
)
