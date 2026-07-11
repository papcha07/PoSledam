package model.geo

import kotlinx.serialization.Serializable

/**
 * Ответ Geoapify Reverse/Forward Geocoding.
 * https://apidocs.geoapify.com/docs/geocoding/reverse-geocoding/
 * Неизвестные поля игнорируются (Json.ignoreUnknownKeys в GeoapifyKtorClient).
 */
@Serializable
data class GeoapifyReverseResponse(
    val features: List<GeoapifyFeature> = emptyList()
)

@Serializable
data class GeoapifyFeature(
    val properties: GeoapifyProperties? = null,
    val geometry: GeoapifyGeometry? = null
)

@Serializable
data class GeoapifyGeometry(
    /** GeoJSON порядок: [lon, lat]. */
    val coordinates: List<Double> = emptyList()
)

@Serializable
data class GeoapifyProperties(
    val formatted: String? = null,
    val city: String? = null,
    val town: String? = null,
    val village: String? = null,
    val county: String? = null,
    val district: String? = null,
    val suburb: String? = null,
    val street: String? = null,
    val housenumber: String? = null,
    val postcode: String? = null,
    val country: String? = null,
    val state: String? = null,
    val lon: Double? = null,
    val lat: Double? = null
)
