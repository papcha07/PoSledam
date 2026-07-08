import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.encodedPath
import model.geo.AddressSuggestion
import model.geo.GeoapifyProperties
import model.geo.GeoapifyReverseResponse

/**
 * Reverse/forward geocoding через Geoapify.
 * Заменяет [YandexGeocodeService] за тем же контрактом (ResultWrapper<AddressSuggestion>).
 *
 * ВНИМАНИЕ по координатам: Geoapify ждёт lat = широта, lon = долгота.
 * Метод принимает (lon, lat) как и раньше — маппим их на нужные параметры без перестановки.
 */
class GeoapifyGeocodeService(
    private val client: HttpClient,
    private val apiKey: String
) {

    suspend fun geocode(
        query: String,
        lang: String = "ru_RU"
    ): ResultWrapper<List<AddressSuggestion>> {

        val result = safeCall<GeoapifyReverseResponse> {
            client.get {
                url {
                    encodedPath = "/v1/geocode/search"
                    parameters.append("text", query)
                    parameters.append("lang", lang.toGeoapifyLang())
                    parameters.append("apiKey", apiKey)
                }
                accept(ContentType.Application.Json)
            }.body()
        }

        return when (result) {
            is ResultWrapper.Success -> {
                val list = result.data.features.mapNotNull { feature ->
                    feature.properties?.toSuggestionOrNull(
                        lon = feature.geometry?.coordinates?.getOrNull(0),
                        lat = feature.geometry?.coordinates?.getOrNull(1)
                    )
                }
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
        lang: String = "ru_RU"
    ): ResultWrapper<AddressSuggestion> {

        val result = safeCall<GeoapifyReverseResponse> {
            client.get {
                url {
                    encodedPath = "/v1/geocode/reverse"
                    parameters.append("lat", lat.toString()) // широта
                    parameters.append("lon", lon.toString()) // долгота
                    parameters.append("lang", lang.toGeoapifyLang())
                    parameters.append("apiKey", apiKey)
                }
                accept(ContentType.Application.Json)
            }.body()
        }

        return when (result) {
            is ResultWrapper.Success -> {
                val feature = result.data.features.firstOrNull()
                val addr = feature?.properties?.toSuggestionOrNull(
                    lon = feature.geometry?.coordinates?.getOrNull(0) ?: lon,
                    lat = feature.geometry?.coordinates?.getOrNull(1) ?: lat
                )
                if (addr != null) ResultWrapper.Success(addr)
                else ResultWrapper.UnknownError(Throwable("No address found for point"))
            }

            is ResultWrapper.HttpError -> result
            is ResultWrapper.NetworkError -> result
            is ResultWrapper.UnknownError -> result
        }
    }
}

/** Geoapify ждёт двухбуквенный код языка ("ru"), а не "ru_RU". */
private fun String.toGeoapifyLang(): String = substringBefore('_').lowercase()

private fun GeoapifyProperties.toSuggestionOrNull(
    lon: Double?,
    lat: Double?
): AddressSuggestion? {
    val addressText = formatted?.takeIf { it.isNotBlank() }
        ?: listOfNotNull(
            street,
            housenumber,
            district ?: suburb,
            city ?: town ?: village,
            state,
            country
        ).joinToString(", ").takeIf { it.isNotBlank() }
        ?: return null

    return AddressSuggestion(
        city = city ?: town ?: village,
        district = district ?: suburb ?: county,
        house = housenumber,
        address = addressText,
        lon = this.lon ?: lon,
        lat = this.lat ?: lat
    )
}
