package data

import apiService.models.petmarket_models.AgeCategoryResponse
import apiService.models.petmarket_models.AllergenResponse
import apiService.models.petmarket_models.AnimalTypeResponse
import apiService.models.petmarket_models.ClothingDetailsResponse
import apiService.models.petmarket_models.FoodDetailsResponse
import apiService.models.petmarket_models.FoodTypeResponse
import apiService.models.petmarket_models.ProductCategoryResponse
import apiService.models.petmarket_models.ProductResponse
import apiService.models.petmarket_models.SeasonResponse
import apiService.models.petmarket_models.SellerResponse
import apiService.models.petmarket_models.SellerShortResponse
import apiService.models.petmarket_models.SellerTypeResponse
import apiService.models.petmarket_models.VariantResponse
import domain.model.MarketAllergen
import domain.model.MarketAnimalType
import domain.model.MarketCategory
import domain.model.MarketClothingDetails
import domain.model.MarketFilter
import domain.model.MarketFoodDetails
import domain.model.MarketProduct
import domain.model.MarketSeller
import domain.model.MarketSellerShort
import domain.model.MarketVariant

fun MarketFilter.toCategoryResponse(): ProductCategoryResponse? = when (category) {
    MarketCategory.Food -> ProductCategoryResponse.FOOD
    MarketCategory.Clothing -> ProductCategoryResponse.CLOTHING
    null -> null
}

fun MarketFilter.toAnimalResponse(): AnimalTypeResponse? = when (animalType) {
    MarketAnimalType.Dog -> AnimalTypeResponse.DOG
    MarketAnimalType.Cat -> AnimalTypeResponse.CAT
    MarketAnimalType.Rodent -> AnimalTypeResponse.RODENT
    MarketAnimalType.Bird -> AnimalTypeResponse.BIRD
    MarketAnimalType.Other -> AnimalTypeResponse.OTHER
    null -> null
}

fun ProductResponse.toMarketProduct() = MarketProduct(
    id = id,
    category = category.toMarketCategory(),
    name = name,
    description = description,
    animalType = animalType.toMarketAnimalType(),
    price = price,
    currency = currency,
    externalUrl = externalUrl,
    images = images,
    variants = variants.map { it.toMarketVariant() },
    foodDetails = foodDetails?.toMarketFoodDetails(),
    clothingDetails = clothingDetails?.toMarketClothingDetails(),
    seller = seller.toMarketSellerShort()
)

fun SellerResponse.toMarketSeller() = MarketSeller(
    id = id,
    brandName = brandName,
    description = description,
    sellerType = sellerType.title(),
    logoUrl = logoUrl,
    websiteUrl = websiteUrl,
    email = email,
    phone = phone,
    city = city,
    verified = verified
)

fun AllergenResponse.toMarketAllergen() = MarketAllergen(id = id, name = name)

private fun ProductCategoryResponse.toMarketCategory() = when (this) {
    ProductCategoryResponse.FOOD -> MarketCategory.Food
    ProductCategoryResponse.CLOTHING -> MarketCategory.Clothing
}

private fun AnimalTypeResponse.toMarketAnimalType() = when (this) {
    AnimalTypeResponse.DOG -> MarketAnimalType.Dog
    AnimalTypeResponse.CAT -> MarketAnimalType.Cat
    AnimalTypeResponse.RODENT -> MarketAnimalType.Rodent
    AnimalTypeResponse.BIRD -> MarketAnimalType.Bird
    AnimalTypeResponse.OTHER -> MarketAnimalType.Other
}

private fun SellerShortResponse.toMarketSellerShort() = MarketSellerShort(
    id = id,
    brandName = brandName,
    logoUrl = logoUrl,
    websiteUrl = websiteUrl,
    verified = verified
)

private fun VariantResponse.toMarketVariant() = MarketVariant(
    id = id,
    label = label,
    price = price,
    inStock = inStock
)

private fun FoodDetailsResponse.toMarketFoodDetails() = MarketFoodDetails(
    composition = composition,
    foodType = foodType.title(),
    ageCategory = ageCategory.title(),
    proteinPercent = proteinPercent,
    fatPercent = fatPercent,
    fiberPercent = fiberPercent,
    moisturePercent = moisturePercent,
    feedingGuide = feedingGuide,
    allergens = allergens.map { it.toMarketAllergen() }
)

private fun ClothingDetailsResponse.toMarketClothingDetails() = MarketClothingDetails(
    material = material,
    season = season.title(),
    handmade = handmade,
    careInstructions = careInstructions,
    sizeChartUrl = sizeChartUrl
)

private fun SellerTypeResponse.title() = when (this) {
    SellerTypeResponse.BREEDER -> "Заводчик"
    SellerTypeResponse.MANUFACTURER -> "Производитель"
    SellerTypeResponse.DESIGNER -> "Дизайнер"
}

private fun FoodTypeResponse.title() = when (this) {
    FoodTypeResponse.DRY -> "Сухой"
    FoodTypeResponse.WET -> "Влажный"
    FoodTypeResponse.TREATS -> "Лакомства"
    FoodTypeResponse.RAW -> "Натуралка"
}

private fun AgeCategoryResponse.title() = when (this) {
    AgeCategoryResponse.PUPPY -> "Для малышей"
    AgeCategoryResponse.ADULT -> "Взрослые"
    AgeCategoryResponse.SENIOR -> "Пожилые"
    AgeCategoryResponse.ALL -> "Любой возраст"
}

private fun SeasonResponse.title() = when (this) {
    SeasonResponse.WINTER -> "Зима"
    SeasonResponse.DEMI -> "Демисезон"
    SeasonResponse.SUMMER -> "Лето"
    SeasonResponse.ALL -> "Любой сезон"
}
