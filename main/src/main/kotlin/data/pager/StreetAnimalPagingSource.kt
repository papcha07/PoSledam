package data.pager

import ApiResponse
import androidx.paging.PagingSource
import androidx.paging.PagingState
import apiService.models.StreetListRequest
import apiService.StreetService
import apiService.models.street_models.StreetAnimalResponse
import domain.model.StreetAnimalParams

class StreetAnimalPagingSource(
    private val service: StreetService,
    private val streetParams: StreetAnimalParams
) : PagingSource<String, StreetAnimalResponse>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, StreetAnimalResponse> {
        return try {
            val request = StreetListRequest(
                lastDateTime = params.key,
                centerRadius = streetParams.centerRadius,
                searchCenterLatitude = streetParams.searchCenterLatitude,
                searchCenterLongitude = streetParams.searchCenterLongitude
            )            
            when (val response = service.getStreetAnimals(request)) {
                is ApiResponse.Error -> {
                    LoadResult.Error(Exception("Api error"))
                }
                is ApiResponse.Success<List<StreetAnimalResponse>> -> {
                    val animals = response.data
                    val nextKey = animals.lastOrNull()?.createdAt

                    LoadResult.Page(
                        data = animals,
                        prevKey = null,
                        nextKey = nextKey
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, StreetAnimalResponse>): String? {
        return null
    }
}
