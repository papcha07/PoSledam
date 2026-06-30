package domain.models

sealed interface ReportAnnouncementResult {
    data object Success : ReportAnnouncementResult
    data object AlreadyReported : ReportAnnouncementResult
    data object Unauthorized : ReportAnnouncementResult
    data object Forbidden : ReportAnnouncementResult
    data object NotFound : ReportAnnouncementResult
    data object NoInternet : ReportAnnouncementResult
    data object Error : ReportAnnouncementResult
}
