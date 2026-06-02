package di

import data.SearchRepositoryImpl
import domain.interactor.SearchInteractor
import domain.interactor.SearchInteractorImpl
import domain.repository.SearchRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.viewModel.FilterViewModel
import ui.viewModel.ReportViewModel

fun getSearchRepository() = module {
    single<SearchRepository> {
        SearchRepositoryImpl(
            announcementService = get(),
            converter = get()
        )
    }
}

fun getSearchInteractor() = module {
    single<SearchInteractor> {
        SearchInteractorImpl(
            repository = get()
        )
    }
}


fun getFilterViewModel() = module {
    viewModel {
        FilterViewModel(
            get(),
            get()
        )
    }

    viewModel {
        ReportViewModel(
            searchInteractor = get()
        )
    }
}
