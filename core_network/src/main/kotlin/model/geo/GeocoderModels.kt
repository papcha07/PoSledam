package model.geo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocoderResponse(
    val features: List<GeoFeature> = emptyList()
)

@Serializable
data class GeoFeature(
    val properties: GeoProperties? = null,
    val geometry: GeoGeometry? = null
)

@Serializable
data class GeoGeometry(
    val type: String? = null,
    val coordinates: List<Double> = emptyList()
)

@Serializable
data class GeoProperties(
    val GeocoderMetaData: GeocoderMetaData? = null
)

@Serializable
data class GeocoderMetaData(
    val Address: GeoAddress? = null,
    val text: String? = null
)

@Serializable
data class GeoAddress(
    val formatted: String? = null,
    @SerialName("Components") val components: List<GeoComponent> = emptyList()
)

@Serializable
data class GeoComponent(
    val kind: String? = null,
    val name: String? = null
)
