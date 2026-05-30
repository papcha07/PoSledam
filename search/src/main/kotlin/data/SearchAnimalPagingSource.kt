package data

import apiService.AnnouncementService
import model.announcement.MissAllRequest

class SearchAnimalPagingSource(
    private val announcementService: AnnouncementService,
    private val filter: MissAllRequest,
    private val type: SearchAnimalType
) {


}