package di

import data.AiSearchRepositoryImpl
import domain.interactor.AiSearchInteractor
import domain.interactor.AiSearchInteractorImpl
import domain.repository.AiSearchRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.viewModel.AiSearchResultViewModel
import ui.viewModel.AiSearchViewModel

fun getAiSearchRepository() = module {
    single<AiSearchRepository> {
        AiSearchRepositoryImpl(
            service = get(),
            converter = get()
        )
    }
}

fun getAiSearchInteractor() = module {
    single<AiSearchInteractor> {
        AiSearchInteractorImpl(
            repository = get()
        )
    }
}

fun getAiSearchViewModel() = module {
    viewModel {
        AiSearchViewModel(
            interactor = get(),
            locationProvider = get()
        )
    }

    viewModel {
        AiSearchResultViewModel(
            interactor = get()
        )
    }
}
