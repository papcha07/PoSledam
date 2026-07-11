package di

import data.MarketRepositoryImpl
import domain.interactor.MarketInteractor
import domain.interactor.MarketInteractorImpl
import domain.repository.MarketRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.viewModel.MarketCatalogViewModel
import ui.viewModel.MarketProductDetailsViewModel
import ui.viewModel.MarketSellerViewModel

fun getMarketRepository() = module {
    single<MarketRepository> {
        MarketRepositoryImpl(
            service = get()
        )
    }
}

fun getMarketInteractor() = module {
    single<MarketInteractor> {
        MarketInteractorImpl(
            repository = get()
        )
    }
}

fun getMarketViewModel() = module {
    viewModel {
        MarketCatalogViewModel(
            interactor = get()
        )
    }

    viewModel {
        MarketProductDetailsViewModel(
            interactor = get()
        )
    }

    viewModel {
        MarketSellerViewModel(
            interactor = get()
        )
    }
}
