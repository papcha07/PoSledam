package data.repository

import apiService.AuthService
import domain.repository.ImageLoaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImageLoaderRepositoryImpl(
    private val authService: AuthService
) : ImageLoaderRepository {

    override suspend fun loadImage(file: File, id: String) = withContext(Dispatchers.IO) {
        authService.updateUserImage(file, id)
    }
}