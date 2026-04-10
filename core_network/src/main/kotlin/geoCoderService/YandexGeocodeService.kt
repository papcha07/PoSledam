import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.encodedPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.geo.AddressSuggestion

class YandexGeocodeService(
    private val client: HttpClient,
    private val apiKey: String
) {
    suspend fun geocode(
        query: String,
        lang: String = "ru_RU"
    ): ResultWrapper<List<AddressSuggestion>> {

        val result = safeCall<GeocoderResponseV1> {
            client.get {
                url {
                    encodedPath = "/v1/"
                    parameters.append("apikey", apiKey)
                    parameters.append("geocode", query)
                    parameters.append("lang", lang)
                    parameters.append("format", "json")
                }
                accept(ContentType.Application.Json)
            }.body()
        }

        return when (result) {
            is ResultWrapper.Success -> {
                val list = result.data.response.GeoObjectCollection.featureMember
                    .mapNotNull { it.GeoObject.toSuggestionOrNull() }
                ResultWrapper.Success(list)
            }

            is ResultWrapper.HttpError -> result
            is ResultWrapper.NetworkError -> result
            is ResultWrapper.UnknownError -> result
        }
    }

    suspend fun reverseGeocodeOne(
        lon: Double,
        lat: Double,
        lang: String = "ru_RU",
        kind: String = "house"
    ): ResultWrapper<AddressSuggestion> {

        val result = safeCall<GeocoderResponseV1> {
            client.get {
                url {
                    encodedPath = "/v1/"
                    parameters.append("apikey", apiKey)
                    parameters.append("geocode", "$lon,$lat") // lon,lat
                    parameters.append("kind", kind)
                    parameters.append("results", "1")
                    parameters.append("lang", lang)
                    parameters.append("format", "json")
                }
                accept(ContentType.Application.Json)
            }.body()
        }

        return when (result) {
            is ResultWrapper.Success -> {
                val addr = result.data.response.GeoObjectCollection.featureMember
                    .firstOrNull()
                    ?.GeoObject
                    ?.toSuggestionOrNull()
                    ?.let { s -> s.copy(lon = s.lon ?: lon, lat = s.lat ?: lat) }

                if (addr != null) ResultWrapper.Success(addr)
                else ResultWrapper.UnknownError(Throwable("No address found for point"))
            }

            is ResultWrapper.HttpError -> result
            is ResultWrapper.NetworkError -> result
            is ResultWrapper.UnknownError -> result
        }
    }
}


@kotlinx.serialization.Serializable
data class GeocoderResponseV1(
    val response: Response
) {
    @kotlinx.serialization.Serializable
    data class Response(
        val GeoObjectCollection: GeoObjectCollection
    )

    @kotlinx.serialization.Serializable
    data class GeoObjectCollection(
        val featureMember: List<FeatureMember> = emptyList()
    )

    @kotlinx.serialization.Serializable
    data class FeatureMember(
        val GeoObject: GeoObject
    )

    @kotlinx.serialization.Serializable
    data class GeoObject(
        val name: String? = null,
        val description: String? = null,
        val Point: Point? = null,
        val metaDataProperty: MetaDataProperty? = null
    )

    @kotlinx.serialization.Serializable
    data class Point(
        /** строка вида "lon lat" */
        val pos: String
    )

    @kotlinx.serialization.Serializable
    data class MetaDataProperty(
        val GeocoderMetaData: GeocoderMetaData
    )

    @kotlinx.serialization.Serializable
    data class GeocoderMetaData(
        val text: String? = null,
        val Address: Address? = null
    )

    @kotlinx.serialization.Serializable
    data class Address(
        val formatted: String? = null,
        @SerialName("Components") val components: List<Component> = emptyList()
    )

    @Serializable
    data class Component(
        val kind: String? = null,
        val name: String? = null
    )
}

private fun GeocoderResponseV1.GeoObject.toSuggestionOrNull(): AddressSuggestion? {
    // coords из "lon lat"
    val (lon, lat) = Point?.pos
        ?.split(" ")
        ?.mapNotNull { it.toDoubleOrNull() }
        ?.let { it.getOrNull(0) to it.getOrNull(1) }
        ?: return null

    var city: String? = null
    var district: String? = null
    var house: String? = null

    metaDataProperty?.GeocoderMetaData?.Address?.components?.forEach { c ->
        when (c.kind?.lowercase()) {
            "locality" -> if (city == null) city = c.name
            "district" -> if (district == null) district = c.name
            "house" -> if (house == null) house = c.name
        }
    }

    val addressText = metaDataProperty?.GeocoderMetaData?.text
        ?: metaDataProperty?.GeocoderMetaData?.Address?.formatted
        ?: return null

    return AddressSuggestion(
        city = city,
        district = district,
        house = house,
        address = addressText,
        lon = lon,
        lat = lat
    )
}


