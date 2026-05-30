package data

import ApiResponse
import androidx.paging.PagingSource
import androidx.paging.PagingState
import apiService.AnnouncementService
import domain.models.PetUiPreview
import model.announcement.MissAllDto
import model.announcement.MissAllRequest

class SearchAnimalPagingSource(
    private val announcementService: AnnouncementService,
    private val filter: MissAllRequest,
    private val type: SearchAnimalType
) : PagingSource<String, PetUiPreview>() {

    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, PetUiPreview> {
        return try {
            val lastDateTime = params.key

            val request = filter.copy(
                lastDateTime = lastDateTime
            )

            val pets: List<PetUiPreview> = when (type) {
                SearchAnimalType.Found -> {
                    when (val response = announcementService.findFoundAnnouncement(request)) {
                        is ApiResponse.Success -> {
                            response.data.map { it.toPetUiPreview() }
                        }

                        is ApiResponse.Error -> {
                            return LoadResult.Error(
                                Exception("Ошибка загрузки")
                            )
                        }
                    }
                }

                SearchAnimalType.Missing -> {
                    when (val response = announcementService.findMissingAnnouncement(request)) {
                        is ApiResponse.Success -> {
                            response.data.map { it.toPetUiPreview() }
                        }

                        is ApiResponse.Error -> {
                            return LoadResult.Error(
                                Exception("Ошибка загрузки")
                            )
                        }
                    }
                }
            }

            val nextKey = pets.lastOrNull()?.createdAt

            LoadResult.Page(
                data = pets,
                prevKey = null,
                nextKey = if (pets.isEmpty()) null else nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(
        state: PagingState<String, PetUiPreview>
    ): String? {
        return null
    }
}

private fun MissAllDto.toPetUiPreview(): PetUiPreview {
    return PetUiPreview(
        id = id,
        imageUrl = mainImagePath,
        district = district,
        createdAt = createdAt,
        petName = petName,
        description = description,
        breed = breed,
    )
}