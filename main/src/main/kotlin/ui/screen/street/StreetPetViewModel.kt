package ui.screen.street

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import domain.interactor.street.StreetPetInteractor
import domain.model.StreetAnimalParams
import domain.models.ReportAnnouncementResult
import domain.models.StreetDetails
import domain.models.StreetPetPreviewModel
import domain.user.UserInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ui.model.ScreenState

data class StreetReportUiState(
    val isReportBottomSheetVisible: Boolean = false,
    val reportComment: String = "",
    val isReportLoading: Boolean = false
)

sealed interface StreetReportEffect {
    data class Message(val message: String) : StreetReportEffect
}

class StreetPetViewModel(
    private val streetPetInteractor: StreetPetInteractor,
    private val userInteractor: UserInteractor
) : ViewModel() {

    private val _detailsState = MutableStateFlow<ScreenState<StreetDetails>>(ScreenState.Idle)
    val detailsState = _detailsState.asStateFlow()

    private val _reportUiState = MutableStateFlow(StreetReportUiState())
    val reportUiState = _reportUiState.asStateFlow()

    private val _reportEffect = MutableSharedFlow<StreetReportEffect>()
    val reportEffect = _reportEffect.asSharedFlow()

    val userState = userInteractor.observeUser()

    @OptIn(ExperimentalCoroutinesApi::class)
    val streetAnimals: Flow<PagingData<StreetPetPreviewModel>> =
        userInteractor
            .observeLocation()
            .distinctUntilChanged()
            .flatMapLatest { location ->
                streetPetInteractor.getStreetAnimals(
                    StreetAnimalParams(
                        centerRadius = location?.let { DEFAULT_RADIUS },
                        searchCenterLatitude = location?.latitude,
                        searchCenterLongitude = location?.longitude
                    )
                )
            }
            .cachedIn(viewModelScope)

    fun getDetailsAboutAnimal(id: String) {
        viewModelScope.launch {
            _detailsState.value = ScreenState.Loading
            val request = streetPetInteractor.getInfoAboutStreetAnimal(id)
            val animal = request.first
            if (animal != null) {
                _detailsState.value = ScreenState.Success(animal)
                return@launch
            }
            when (request.second) {
                400 -> _detailsState.value = ScreenState.Error
                -1 -> _detailsState.value = ScreenState.InternetError
            }
        }
    }

    fun openReportBottomSheet() {
        _reportUiState.value = _reportUiState.value.copy(
            isReportBottomSheetVisible = true
        )
    }

    fun closeReportBottomSheet() {
        _reportUiState.value = StreetReportUiState()
    }

    fun updateReportComment(comment: String) {
        _reportUiState.value = _reportUiState.value.copy(
            reportComment = comment.take(REPORT_COMMENT_LIMIT)
        )
    }

    fun reportAnnouncement(
        announcementId: String,
        announcementOwnerId: String,
        currentUserId: String?
    ) {
        val state = _reportUiState.value
        val trimmedComment = state.reportComment.trim()

        if (currentUserId?.takeIf { it.isNotBlank() } == announcementOwnerId) {
            emitReportMessage(OWN_ANNOUNCEMENT_MESSAGE)
            return
        }

        if (state.isReportLoading) {
            return
        }

        if (trimmedComment.isBlank()) {
            emitReportMessage(EMPTY_COMMENT_MESSAGE)
            return
        }

        viewModelScope.launch {
            _reportUiState.value = state.copy(isReportLoading = true)
            val result = streetPetInteractor.reportAnnouncement(
                announcementId = announcementId,
                comment = trimmedComment
            )

            if (result == ReportAnnouncementResult.Success) {
                _reportUiState.value = StreetReportUiState()
            } else {
                _reportUiState.value = _reportUiState.value.copy(isReportLoading = false)
            }
            _reportEffect.emit(StreetReportEffect.Message(result.toMessage()))
        }
    }

    private fun emitReportMessage(message: String) {
        viewModelScope.launch {
            _reportEffect.emit(StreetReportEffect.Message(message))
        }
    }

    companion object {
        private const val DEFAULT_RADIUS = 40
        const val REPORT_COMMENT_LIMIT = 500
        private const val EMPTY_COMMENT_MESSAGE = "Опишите причину жалобы"
        private const val OWN_ANNOUNCEMENT_MESSAGE = "Нельзя пожаловаться на своё объявление"
    }
}

private fun ReportAnnouncementResult.toMessage(): String {
    return when (this) {
        ReportAnnouncementResult.Success -> "Жалоба отправлена"
        ReportAnnouncementResult.AlreadyReported -> "Вы уже отправляли жалобу на это объявление"
        ReportAnnouncementResult.Unauthorized -> "Необходимо войти в аккаунт"
        ReportAnnouncementResult.Forbidden -> "У вас нет доступа к этому действию"
        ReportAnnouncementResult.NotFound -> "Объявление не найдено"
        ReportAnnouncementResult.NoInternet -> "Проверьте подключение к интернету"
        ReportAnnouncementResult.Error -> "Не удалось отправить жалобу. Попробуйте позже"
    }
}
