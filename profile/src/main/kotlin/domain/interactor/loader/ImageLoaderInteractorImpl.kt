package domain.interactor.loader

import domain.repository.ImageLoaderRepository
import ui.other.Converter

class ImageLoaderInteractorImpl(
    private val loaderRepository: ImageLoaderRepository,
    private val converter: Converter
) : ImageLoaderInteractor {

    override suspend fun loadImage(uri: String, id: String) {
        val convertedFile = converter.convertToFile(uri)
        loaderRepository.loadImage(convertedFile, id)
    }
}