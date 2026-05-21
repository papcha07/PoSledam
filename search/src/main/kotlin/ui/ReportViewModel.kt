package ui

import androidx.lifecycle.ViewModel
import domain.interactor.SearchInteractor

class ReportViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

}
