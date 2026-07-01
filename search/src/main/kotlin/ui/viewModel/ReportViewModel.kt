package ui.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactor.SearchInteractor
import domain.models.REPORT_PHOTO_LIMIT
import domain.models.ReportAnnouncementResult
import domain.user.UserInteractor
import domain.user.model.LocationDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.model.Response

data class ReportFoundAnimalUiState(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false
)

data class ReportAnnouncementUiState(
    val isReportBottomSheetVisible: Boolean = false,
    val reportComment: String = "",
    val isReportLoading: Boolean = false
)

sealed interface ReportFoundAnimalEffect {
    data object InternetError : ReportFoundAnimalEffect
    data object ServerError : ReportFoundAnimalEffect
    data class AnnouncementReportMessage(val message: String) : ReportFoundAnimalEffect
}

data class SpottedAnimalData(
    val lon: Double? = null,
    val lat: Double? = null,
    val uri: List<Uri> = emptyList()
)

class ReportViewModel(
    private val searchInteractor: SearchInteractor,
    userInteractor: UserInteractor
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportFoundAnimalUiState())
    val uiState = _uiState.asStateFlow()

    val mapCameraLocation: StateFlow<LocationDto?> =
        userInteractor
            .observeLocation()
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    private val _effect = MutableSharedFlow<ReportFoundAnimalEffect>()
    val effect = _effect.asSharedFlow()

    private val _reportAnnouncementState = MutableStateFlow(ReportAnnouncementUiState())
    val reportAnnouncementState = _reportAnnouncementState.asStateFlow()

    private val _spottedUiState = MutableStateFlow<SpottedAnimalData>(SpottedAnimalData())
    val spottedAnimalData = _spottedUiState.asStateFlow()


    private val _findUriState = MutableStateFlow<List<Uri>>(listOf())
    val findUriState = _findUriState.asStateFlow()

    fun reportFoundAnimal(id: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val response = searchInteractor.reportFoundAnimal(id, _findUriState.value)
            when (response) {
                Response.SUCCESS -> {
                    _uiState.update { it.copy(isSuccess = true) }
                }

                Response.INTERNET_ERROR -> {
                    _effect.emit(ReportFoundAnimalEffect.InternetError)
                }

                Response.SERVER_ERROR -> {
                    _effect.emit(ReportFoundAnimalEffect.ServerError)
                }
            }
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun reportSpottedAnimal(id: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val spottedData = _spottedUiState.value
            val response = searchInteractor.reportSpottedAnimal(id, spottedData)
            when (response) {
                Response.SUCCESS -> {
                    _uiState.update { it.copy(isSuccess = true) }
                }

                Response.INTERNET_ERROR -> {
                    _effect.emit(ReportFoundAnimalEffect.InternetError)
                }

                Response.SERVER_ERROR -> {
                    _effect.emit(ReportFoundAnimalEffect.ServerError)
                }
            }
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun openReportAnnouncementSheet() {
        _reportAnnouncementState.update {
            it.copy(isReportBottomSheetVisible = true)
        }
    }

    fun closeReportAnnouncementSheet() {
        _reportAnnouncementState.update {
            it.copy(
                isReportBottomSheetVisible = false,
                reportComment = "",
                isReportLoading = false
            )
        }
    }

    fun updateReportAnnouncementComment(comment: String) {
        _reportAnnouncementState.update {
            it.copy(reportComment = comment.take(REPORT_ANNOUNCEMENT_COMMENT_LIMIT))
        }
    }

    fun reportAnnouncement(
        announcementId: String,
        announcementOwnerId: String,
        currentUserId: String?
    ) {
        val state = _reportAnnouncementState.value
        val trimmedComment = state.reportComment.trim()
        if (currentUserId?.takeIf { it.isNotBlank() } == announcementOwnerId) {
            viewModelScope.launch {
                _effect.emit(
                    ReportFoundAnimalEffect.AnnouncementReportMessage(OWN_ANNOUNCEMENT_MESSAGE)
                )
            }
            return
        }

        if (state.isReportLoading) {
            return
        }

        if (trimmedComment.isBlank()) {
            viewModelScope.launch {
                _effect.emit(
                    ReportFoundAnimalEffect.AnnouncementReportMessage(EMPTY_COMMENT_MESSAGE)
                )
            }
            return
        }

        viewModelScope.launch {
            _reportAnnouncementState.update {
                it.copy(isReportLoading = true)
            }

            val result = searchInteractor.reportAnnouncement(announcementId, trimmedComment)
            when (result) {
                ReportAnnouncementResult.Success -> {
                    _reportAnnouncementState.update {
                        it.copy(
                            isReportBottomSheetVisible = false,
                            reportComment = "",
                            isReportLoading = false
                        )
                    }
                    _effect.emit(
                        ReportFoundAnimalEffect.AnnouncementReportMessage(REPORT_SUCCESS_MESSAGE)
                    )
                }

                else -> {
                    _reportAnnouncementState.update {
                        it.copy(isReportLoading = false)
                    }
                    _effect.emit(
                        ReportFoundAnimalEffect.AnnouncementReportMessage(
                            result.toReportAnnouncementMessage()
                        )
                    )
                }
            }
        }
    }

    fun updateLatitude(lat: Double) {
        _spottedUiState.update {
            it.copy(lat = lat)
        }
    }

    fun updateLongitude(lon: Double) {
        _spottedUiState.update {
            it.copy(lon = lon)
        }
    }

    fun addImage(uri: Uri) {
        addImages(listOf(uri))
    }

    fun addImages(uris: List<Uri>) {
        _spottedUiState.update { state ->
            val remainingSlots = (REPORT_PHOTO_LIMIT - state.uri.size).coerceAtLeast(0)
            state.copy(
                uri = state.uri + uris.take(remainingSlots)
            )
        }
    }

    fun addFindImage(uri: Uri) {
        addFindImages(listOf(uri))
    }

    fun addFindImages(uris: List<Uri>) {
        _findUriState.update { state ->
            val remainingSlots = (REPORT_PHOTO_LIMIT - state.size).coerceAtLeast(0)
            state + uris.take(remainingSlots)
        }
    }

    fun removeImage(uri: Uri) {
        _spottedUiState.update { state ->
            state.copy(uri = state.uri - uri)
        }
    }

    fun removeFindImage(uri: Uri) {
        _findUriState.update { state ->
            state - uri
        }
    }

    companion object {
        const val REPORT_ANNOUNCEMENT_COMMENT_LIMIT = 500
        private const val REPORT_SUCCESS_MESSAGE = "Жалоба отправлена"
        private const val EMPTY_COMMENT_MESSAGE = "Опишите причину жалобы"
        private const val OWN_ANNOUNCEMENT_MESSAGE = "Нельзя пожаловаться на своё объявление"
        private const val ALREADY_REPORTED_MESSAGE =
            "Вы уже отправляли жалобу на это объявление"
        private const val UNAUTHORIZED_MESSAGE = "Необходимо войти в аккаунт"
        private const val FORBIDDEN_MESSAGE = "У вас нет доступа к этому действию"
        private const val NOT_FOUND_MESSAGE = "Объявление не найдено"
        private const val NO_INTERNET_MESSAGE = "Проверьте подключение к интернету"
        private const val DEFAULT_REPORT_ERROR_MESSAGE =
            "Не удалось отправить жалобу. Попробуйте позже"
    }
}

private fun ReportAnnouncementResult.toReportAnnouncementMessage(): String {
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
