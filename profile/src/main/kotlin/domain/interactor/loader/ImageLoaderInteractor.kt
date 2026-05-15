package domain.interactor.loader

interface ImageLoaderInteractor {
    suspend fun loadImage(uri: String, id: String)
}