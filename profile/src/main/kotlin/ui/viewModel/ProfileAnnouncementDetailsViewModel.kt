package ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.announcement.AnnouncementInteractor
import domain.model.CancelReason
import domain.model.FoundReport
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.InternetStatus

sealed class ProfileAnnouncementDetailsState {
    data object Idle : ProfileAnnouncementDetailsState()
    data object Loading : ProfileAnnouncementDetailsState()
    data class Success(
        val announcement: ProfileAnnouncementDetails,
        val spottedLocations: List<SpottedLocation>,
        val spottedLocationsError: String? = null,
        val foundReports: List<FoundReport> = emptyList(),
        val foundReportsError: String? = null
    ) : ProfileAnnouncementDetailsState()

    data class Failed(val message: String) : ProfileAnnouncementDetailsState()
}

data class CancelAnnouncementState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class ProfileAnnouncementDetailsViewModel(
    private val announcementInteractor: AnnouncementInteractor
) : ViewModel() {

    private val _detailsState =
        MutableStateFlow<ProfileAnnouncementDetailsState>(ProfileAnnouncementDetailsState.Idle)
    val detailsState = _detailsState.asStateFlow()

    private val _cancelState = MutableStateFlow(CancelAnnouncementState())
    val cancelState = _cancelState.asStateFlow()

    fun loadDetails(
        announcementId: String,
        announcementType: Int
    ) {
        viewModelScope.launch {
            _detailsState.update { ProfileAnnouncementDetailsState.Loading }

            val detailsResult = announcementInteractor.getAnnouncementDetails(
                id = announcementId,
                type = announcementType
            )
            val announcement = detailsResult.first

            if (announcement == null) {
                _detailsState.update {
                    ProfileAnnouncementDetailsState.Failed(
                        detailsResult.second.toMessage()
                    )
                }
                return@launch
            }

            if (announcementType != MISSING_ANNOUNCEMENT_TYPE) {
                _detailsState.update {
                    ProfileAnnouncementDetailsState.Success(
                        announcement = announcement,
                        spottedLocations = emptyList(),
                        foundReports = emptyList()
                    )
                }
                return@launch
            }

            val spottedLocationsResult = announcementInteractor.getSpottedLocations(announcementId)
            val foundReportsResult = announcementInteractor.getFoundReports(announcementId)
            _detailsState.update {
                ProfileAnnouncementDetailsState.Success(
                    announcement = announcement,
                    spottedLocations = spottedLocationsResult.first.orEmpty(),
                    spottedLocationsError = spottedLocationsResult.second?.toMessage(),
                    foundReports = foundReportsResult.first.orEmpty(),
                    foundReportsError = foundReportsResult.second?.toMessage()
                )
            }
        }

    }

    fun cancelAnnouncement(reasonId: Int, announcementType: Int, announcementId: String) {
        if (_cancelState.value.isLoading) return

        viewModelScope.launch {
            _cancelState.update { CancelAnnouncementState(isLoading = true) }

            val result = announcementInteractor.cancelAnnouncement(
                CancelReason(
                    id = announcementId,
                    reason = reasonId,
                    type = announcementType
                )
            )

            _cancelState.update {
                if (result.first) {
                    CancelAnnouncementState(isSuccess = true)
                } else {
                    CancelAnnouncementState(
                        errorMessage = result.second.toMessage()
                    )
                }
            }
        }
    }

    fun clearCancelError() {
        _cancelState.update { state ->
            state.copy(errorMessage = null)
        }
    }

    fun clearCancelResult() {
        _cancelState.update { CancelAnnouncementState() }
    }

    private fun InternetStatus?.toMessage(): String {
        return when (this) {
            InternetStatus.NoInternet -> "Проблемы с интернетом"
            InternetStatus.Error, null -> "Что-то пошло не так"
        }
    }

    private companion object {
        const val MISSING_ANNOUNCEMENT_TYPE = 0
    }
}
