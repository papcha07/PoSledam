package domain.repository

import java.io.File

interface ImageLoaderRepository {
    suspend fun loadImage(file: File, id: String)
}