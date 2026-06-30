package ui.components.photo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import domain.models.REPORT_PHOTO_LIMIT
import java.io.File

data class PhotoAttachmentPickerState(
    val canAddPhotos: Boolean,
    val openPicker: () -> Unit
)

@Composable
fun rememberPhotoAttachmentPickerState(
    selectedPhotoCount: Int,
    onPhotosSelected: (List<Uri>) -> Unit,
    maxPhotos: Int = REPORT_PHOTO_LIMIT
): PhotoAttachmentPickerState {
    val context = LocalContext.current
    val availableSlots = (maxPhotos - selectedPhotoCount).coerceAtLeast(0)
    var showSourceDialog by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var launchCameraAfterPermission by remember { mutableStateOf(false) }

    val singleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && availableSlots > 0) {
            onPhotosSelected(listOf(uri))
        }
    }

    val multipleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = availableSlots.coerceAtLeast(2)
        )
    ) { uris ->
        val selectedUris = uris.take(availableSlots)
        if (selectedUris.isNotEmpty()) {
            onPhotosSelected(selectedUris)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSaved ->
        val uri = pendingCameraUri
        pendingCameraUri = null
        if (isSaved && uri != null && availableSlots > 0) {
            onPhotosSelected(listOf(uri))
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && launchCameraAfterPermission) {
            launchCameraAfterPermission = false
            val uri = context.createCameraImageUri()
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            launchCameraAfterPermission = false
        }
    }

    fun launchGallery() {
        showSourceDialog = false
        val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        if (availableSlots == 1) {
            singleGalleryLauncher.launch(request)
        } else if (availableSlots > 1) {
            multipleGalleryLauncher.launch(request)
        }
    }

    fun launchCamera() {
        showSourceDialog = false
        if (availableSlots <= 0) return
        if (context.shouldRequestCameraPermission()) {
            launchCameraAfterPermission = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        val uri = context.createCameraImageUri()
        pendingCameraUri = uri
        cameraLauncher.launch(uri)
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Добавить фото") },
            text = {
                Column {
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = ::launchGallery
                    ) {
                        Text("Выбрать из галереи")
                    }
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = ::launchCamera
                    ) {
                        Text("Сделать фото")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSourceDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    return PhotoAttachmentPickerState(
        canAddPhotos = availableSlots > 0,
        openPicker = {
            if (availableSlots > 0) {
                showSourceDialog = true
            }
        }
    )
}

private fun Context.createCameraImageUri(): Uri {
    val imageDirectory = File(cacheDir, CAMERA_IMAGE_DIR).apply {
        mkdirs()
    }
    val imageFile = File.createTempFile(CAMERA_IMAGE_PREFIX, CAMERA_IMAGE_SUFFIX, imageDirectory)
    return FileProvider.getUriForFile(
        this,
        "$packageName.fileprovider",
        imageFile
    )
}

private fun Context.shouldRequestCameraPermission(): Boolean {
    return isPermissionDeclared(Manifest.permission.CAMERA) &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
}

private fun Context.isPermissionDeclared(permission: String): Boolean {
    return runCatching {
        packageManager
            .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
            ?.contains(permission) == true
    }.getOrDefault(false)
}

private const val CAMERA_IMAGE_DIR = "camera_images"
private const val CAMERA_IMAGE_PREFIX = "report_photo_"
private const val CAMERA_IMAGE_SUFFIX = ".jpg"
