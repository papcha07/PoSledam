package ui.other

import android.content.Context
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class Converter(private val context: Context) {
    suspend fun convertToFile(uri: String): File = withContext(Dispatchers.IO) {
        val newUri = uri.toUri()
        val mimeType = context.contentResolver.getType(newUri) ?: "image/jpeg"
        val extension = when (mimeType) {
            "image/png" -> ".png"
            "image/jpeg" -> ".jpg"
            else -> ".bin"
        }

        val file = File.createTempFile("upload_", extension, context.cacheDir)

        context.contentResolver.openInputStream(newUri).use { input ->
            requireNotNull(input) { "Unable to open input stream for $newUri" }
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file
    }
}
