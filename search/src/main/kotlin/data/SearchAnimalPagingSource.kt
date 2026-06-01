package data

import ApiResponse
import androidx.paging.PagingSource
import androidx.paging.PagingState
import apiService.AnnouncementService
import domain.models.PetUiPreview
import model.announcement.MissAllRequest

class SearchAnimalPagingSource(
    private val announcementService: AnnouncementService,
    private val filter: MissAllRequest,
    private val type: SearchAnimalType,
) : PagingSource<String, PetUiPreview>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PetUiPreview> {
        return try {
            val lastDateTime = params.key
            val request = filter.copy(lastDateTime = lastDateTime)

            val petList: List<PetUiPreview> = when (type) {
                SearchAnimalType.Found -> {
                    when (val response = announcementService.findFoundAnnouncement(request)) {
                        is ApiResponse.Success -> {
                            response.data.map {
                                it.toPetUiPreview()
                            }
                        }

                        is ApiResponse.Error -> {
                            return LoadResult.Error(
                                Exception("Ошибка загрузки найденных объявлений")
                            )
                        }
                    }
                }

                SearchAnimalType.Missing -> {
                    when (val response = announcementService.findMissingAnnouncement(request)) {
                        is ApiResponse.Success -> {
                            response.data.map { dto ->
                                dto.toPetUiPreview()
                            }
                        }

                        is ApiResponse.Error -> {
                            return LoadResult.Error(
                                Exception("Ошибка загрузки пропавших объявлений")
                            )
                        }
                    }
                }
            }

            val nextKey = if (petList.size < PAGE_SIZE) null else petList.lastOrNull()?.createdAt
            LoadResult.Page(
                data = petList,
                prevKey = null,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, PetUiPreview>): String? {
        return null
    }

    private companion object {
        const val PAGE_SIZE = 20
    }

}